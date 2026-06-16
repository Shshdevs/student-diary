package com.praktika.studentdiary.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.domain.repository.ScheduleRepository
import com.praktika.studentdiary.presentation.events.ScheduleScreenEvents
import com.praktika.studentdiary.presentation.model.ScheduleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: ScheduleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiModel())
    val uiState: StateFlow<ScheduleUiModel> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            when (val state = authRepository.sessionState.first()) {
                is SessionStatus.Authenticated -> {
                    currentUserId = state.session.user?.id
                    loadData()
                }

                else -> {}
            }
        }
    }

    fun onEvent(event: ScheduleScreenEvents) {
        when (event) {
            is ScheduleScreenEvents.LoadData -> viewModelScope.launch { loadData() }
            is ScheduleScreenEvents.CreateSubject -> createSubject(event.name)
            is ScheduleScreenEvents.DeleteSubject -> deleteSubject(event.subjectId)
            is ScheduleScreenEvents.CreateTask -> createTask(
                event.subjectId, event.title, event.type, event.dueDate
            )

            is ScheduleScreenEvents.CompleteTask -> completeTask(event.taskId)
            is ScheduleScreenEvents.OnShowAddTaskDialog -> {
                _uiState.update { it.copy(showAddTaskDialog = event.show) }
            }

            is ScheduleScreenEvents.OnShowAddSubjectDialog -> {
                _uiState.update { it.copy(showAddSubjectDialog = event.show) }
            }
        }
    }

    private suspend fun loadData() {
        currentUserId?.let { userId ->
            _uiState.update { it.copy(isLoading = true, error = null) }

            val subjectsResult = repository.fetchSubjects(userId)
            val tasksResult = repository.fetchTasks(userId)

            if (subjectsResult.isSuccess && tasksResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjects = subjectsResult.getOrNull() ?: emptyList(),
                        tasks = tasksResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = "Ошибка загрузки данных")
                }
            }
        }
    }

    private fun createSubject(name: String) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                _uiState.update { it.copy(isLoading = true) }
                val result = repository.createSubject(userId, name)
                if (result.isSuccess) {
                    _uiState.update { it.copy(showAddSubjectDialog = false) }
                    loadData()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Не удалось создать предмет")
                    }
                }
            }
        }
    }

    private fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.deleteSubject(subjectId)
                result.onSuccess {
                    loadData()
                }
            } catch (e: Exception) {

                Log.e("Error delete", e.toString())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Невозможно удалить предмет: он используется."
                    )
                }
            }
        }
    }

    private fun createTask(subjectId: String, title: String, type: String, dueDate: LocalDateTime) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                _uiState.update { it.copy(isLoading = true) }
                val result = repository.createTask(userId, subjectId, title, type, dueDate)
                if (result.isSuccess) {
                    _uiState.update { it.copy(showAddTaskDialog = false) }
                    loadData()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Не удалось создать задачу")
                    }
                }
            }
        }
    }

    private fun completeTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.completeTask(taskId)
            if (result.isSuccess) {
                loadData()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Ошибка завершения задачи") }
            }
        }
    }
}