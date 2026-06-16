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

    private fun safeLaunch(
        loadingState: (Boolean) -> MaterialsScreenUiModel = { _uiState.value.copy(isLoading = it) },
        isAiAction: Boolean = false,
        errorMessage: String = "Произошла ошибка",
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { loadingState(true) }
            try {
                action()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isGeneratingAi = false,
                        error = "$errorMessage: ${e.message}"
                    )
                }
            }
        }
    }

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
            is MaterialsScreenEvents.SelectMaterial -> _uiState.update { it.copy(selectedMaterial = event.material) }
            is MaterialsScreenEvents.GenerateMaterial -> generateMaterial(
                event.title,
                event.rawText
            )

            is MaterialsScreenEvents.DeleteMaterial -> deleteMaterial(event.materialId)
            is MaterialsScreenEvents.ImportPdf -> importPdf(event.uri, event.fileName)
            is MaterialsScreenEvents.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loadSubjects() {
        val userId = currentUserId ?: return
        safeLaunch(errorMessage = "Ошибка загрузки предметов") {
            val result = scheduleRepository.fetchSubjects(userId)
            val subjects = result.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    isLoading = false,
                    subjects = subjects,
                    selectedSubjectId = subjects.firstOrNull()?.id
                )
            }
            subjects.firstOrNull()?.id?.let { loadMaterialsForSubject(it) }
        }
    }

    private fun selectSubject(subjectId: String) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
        loadMaterialsForSubject(subjectId)
    }

    private fun loadMaterialsForSubject(subjectId: String) {
        safeLaunch(errorMessage = "Ошибка загрузки материалов") {
            val result = materialsRepository.getMaterialsForSubject(subjectId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    materials = result.getOrDefault(emptyList())
                )
            }
        }
    }

    private fun generateMaterial(title: String, rawText: String) {
        val userId = currentUserId ?: return
        val subjectId = _uiState.value.selectedSubjectId ?: return

        safeLaunch(isAiAction = true, errorMessage = "Ошибка генерации ИИ") {
            materialsRepository.processAndSaveNewMaterial(userId, subjectId, title, rawText)
            _uiState.update { it.copy(isGeneratingAi = false) }
            loadMaterialsForSubject(subjectId)
        }
    }

    private fun deleteMaterial(materialId: String) {
        val subjectId = _uiState.value.selectedSubjectId ?: return
        safeLaunch(errorMessage = "Ошибка удаления") {
            materialsRepository.deleteMaterial(materialId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedMaterial = if (it.selectedMaterial?.id == materialId) null else it.selectedMaterial
                )
            }
            loadMaterialsForSubject(subjectId)
        }
    }

    private fun importPdf(uri: Uri, fileName: String) {
        val userId = currentUserId ?: return
        val subjectId = _uiState.value.selectedSubjectId ?: return

        safeLaunch(isAiAction = true, errorMessage = "Ошибка обработки PDF") {
            val rawText = pdfParserSource.extractTextFromPdf(uri)
            if (rawText.isBlank()) throw Exception("PDF пуст")

            materialsRepository.processAndSaveNewMaterial(userId, subjectId, fileName, rawText)
            _uiState.update { it.copy(isGeneratingAi = false) }
            loadMaterialsForSubject(subjectId)
        }
    }
}