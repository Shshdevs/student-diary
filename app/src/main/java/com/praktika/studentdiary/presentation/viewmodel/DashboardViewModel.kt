package com.praktika.studentdiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.domain.repository.DashboardRepository
import com.praktika.studentdiary.presentation.model.DashboardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiModel(isLoading = true))
    val uiState: StateFlow<DashboardUiModel> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val sessionState = authRepository.sessionState.first()
                val userId = if (sessionState is SessionStatus.Authenticated) {
                    sessionState.session.user?.id
                } else null

                if (userId != null) {
                    val result = repository.getDashboardData(userId)

                    result.fold(
                        onSuccess = { dashboardData ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                data = dashboardData
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message
                                    ?: "Произошла ошибка при загрузке дашборда"
                            )
                        }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Пользователь не авторизован"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Неизвестная ошибка"
                )
            }
        }
    }

    fun refreshData() {
        loadDashboardData()
    }
}