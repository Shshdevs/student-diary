package com.praktika.studentdiary.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.data.source.PdfParserSource
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.domain.repository.MaterialsRepository
import com.praktika.studentdiary.domain.repository.ScheduleRepository
import com.praktika.studentdiary.presentation.events.MaterialsScreenEvents
import com.praktika.studentdiary.presentation.model.MaterialsScreenUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialsScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val scheduleRepository: ScheduleRepository,
    private val materialsRepository: MaterialsRepository,
    private val pdfParserSource: PdfParserSource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MaterialsScreenUiModel())
    val uiState: StateFlow<MaterialsScreenUiModel> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            val sessionState = authRepository.sessionState.first()
            if (sessionState is SessionStatus.Authenticated) {
                currentUserId = sessionState.session.user?.id
                onEvent(MaterialsScreenEvents.LoadSubjects)
            } else {
                _uiState.update { it.copy(error = "Пользователь не авторизован") }
            }
        }
    }

    fun onEvent(event: MaterialsScreenEvents) {
        when (event) {
            is MaterialsScreenEvents.LoadSubjects -> loadSubjects()
            is MaterialsScreenEvents.SelectSubject -> selectSubject(event.subjectId)
            is MaterialsScreenEvents.SelectMaterial -> {
                _uiState.update { it.copy(selectedMaterial = event.material) }
            }

            is MaterialsScreenEvents.GenerateMaterial -> generateMaterial(
                event.title,
                event.rawText
            )

            is MaterialsScreenEvents.DeleteMaterial -> deleteMaterial(event.materialId)
            is MaterialsScreenEvents.ImportPdf -> importPdf(event.uri, event.fileName)
            is MaterialsScreenEvents.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun loadSubjects() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result =
                scheduleRepository.fetchSubjects(userId)

            if (result.isSuccess) {
                val subjects = result.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjects = subjects,
                        selectedSubjectId = subjects.firstOrNull()?.id ?: it.selectedSubjectId
                    )
                }
                _uiState.value.selectedSubjectId?.let { loadMaterialsForSubject(it) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun selectSubject(subjectId: String) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
        loadMaterialsForSubject(subjectId)
    }

    private fun loadMaterialsForSubject(subjectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = materialsRepository.getMaterialsForSubject(subjectId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, materials = result.getOrDefault(emptyList()))
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun generateMaterial(title: String, rawText: String) {
        val userId = currentUserId
        val subjectId = _uiState.value.selectedSubjectId

        if (userId == null || subjectId == null) {
            _uiState.update { it.copy(error = "Ошибка: не выбран предмет или нет авторизации") }
            return
        }

        if (rawText.isBlank()) {
            _uiState.update { it.copy(error = "Текст лекции не может быть пустым") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true) }

            val result =
                materialsRepository.processAndSaveNewMaterial(userId, subjectId, title, rawText)

            if (result.isSuccess) {
                _uiState.update { it.copy(isGeneratingAi = false) }
                loadMaterialsForSubject(subjectId)
            } else {
                _uiState.update {
                    it.copy(
                        isGeneratingAi = false,
                        error = "Ошибка генерации ИИ: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    private fun deleteMaterial(materialId: String) {
        val subjectId = _uiState.value.selectedSubjectId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = materialsRepository.deleteMaterial(materialId)

            if (result.isSuccess) {
                if (_uiState.value.selectedMaterial?.id == materialId) {
                    _uiState.update { it.copy(selectedMaterial = null) }
                }
                loadMaterialsForSubject(subjectId)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка удаления: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    private fun importPdf(uri: Uri, fileName: String) {
        val userId = currentUserId
        val subjectId = _uiState.value.selectedSubjectId

        if (userId == null || subjectId == null) {
            _uiState.update { it.copy(error = "Предмет не выбран или нет авторизации") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true) }

            try {
                val rawText = pdfParserSource.extractTextFromPdf(uri)

                if (rawText.isBlank()) {
                    throw Exception("PDF пуст или не содержит текста")
                }
                val result = materialsRepository.processAndSaveNewMaterial(
                    userId = userId,
                    subjectId = subjectId,
                    title = fileName,
                    rawText = rawText
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(isGeneratingAi = false) }
                    loadMaterialsForSubject(subjectId)
                } else {
                    throw Exception(result.exceptionOrNull()?.message ?: "Ошибка API")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingAi = false,
                        error = "Ошибка обработки PDF: ${e.message}"
                    )
                }
            }
        }
    }
}