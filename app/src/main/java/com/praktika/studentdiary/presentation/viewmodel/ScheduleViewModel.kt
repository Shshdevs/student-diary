package com.praktika.studentdiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.domain.repository.DashboardRepository
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
    private val dashboardRepository: DashboardRepository,
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

    private fun safeLaunch(
        errorMessage: String,
        onSuccess: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    private suspend fun loadData() {
        currentUserId?.let { userId ->
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val subjects = repository.fetchSubjects(userId)
                val tasks = repository.fetchTasks(userId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        subjects = subjects.getOrNull() ?: emptyList(),
                        tasks = tasks.getOrNull() ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Ошибка загрузки") }
            }
        }
    }

    private fun deleteSubject(subjectId: String) {
        safeLaunch("Не удалось удалить предмет") {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.deleteSubject(subjectId)
            if (result.isSuccess) {
                loadData()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Не удалось удалить") }
            }
        }
    }

    private fun createSubject(name: String) {
        safeLaunch("Не удалось создать предмет") {
            currentUserId?.let { userId ->
                val result = repository.createSubject(userId, name)
                if (result.isSuccess) {
                    _uiState.update { it.copy(showAddSubjectDialog = false) }
                    loadData() // Сбрасывает isLoading внутри себя
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun createTask(subjectId: String, title: String, type: String, dueDate: LocalDateTime) {
        safeLaunch("Не удалось создать задачу") {
            currentUserId?.let { userId ->
                val result = repository.createTask(userId, subjectId, title, type, dueDate)
                if (result.isSuccess) {
                    _uiState.update { it.copy(showAddTaskDialog = false) }
                    loadData()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun completeTask(taskId: String) {
        safeLaunch("Ошибка завершения задачи") {
            val task = _uiState.value.tasks.find { it.id == taskId }

            val result = repository.completeTask(taskId)
            if (result.isSuccess) {
                task?.let { addTimeLog(it.subjectId, 30) }

                loadData()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addTimeLog(subjectId: String, minutes: Int) {
        viewModelScope.launch {
            try {
                currentUserId?.let { userId ->
                    dashboardRepository.logTime(userId, subjectId, minutes)
                }
            } catch (e: Exception) {
            }
        }
    }
}