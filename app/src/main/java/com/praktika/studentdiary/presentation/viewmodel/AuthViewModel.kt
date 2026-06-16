package com.praktika.studentdiary.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.presentation.events.AuthScreenEvents
import com.praktika.studentdiary.presentation.model.AuthScreenUiModel
import com.praktika.studentdiary.presentation.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _authScreenState = MutableStateFlow(AuthScreenUiModel())
    val authScreenState = _authScreenState.asStateFlow()

    val sessionState = authRepository.sessionState

    fun onEvent(event: AuthScreenEvents) {
        when (event) {
            is AuthScreenEvents.OnEmailChange -> {
                _authScreenState.update { it.copy(email = event.email) }
            }

            is AuthScreenEvents.OnPasswordChange -> {
                _authScreenState.update { it.copy(password = event.password) }

            }

            AuthScreenEvents.OnSignIn -> {
                signIn(_authScreenState.value.email, _authScreenState.value.password)
            }

            AuthScreenEvents.OnSignUp -> {
                signUp(
                    _authScreenState.value.username,
                    _authScreenState.value.email,
                    _authScreenState.value.password
                )
            }

            AuthScreenEvents.OnToggleState -> {
                _authScreenState.update {
                    it.copy(
                        authScreenState = when (it.authScreenState) {
                            AuthScreenUiModel.Companion.AuthScreenState.SignIn -> AuthScreenUiModel.Companion.AuthScreenState.SignUp
                            AuthScreenUiModel.Companion.AuthScreenState.SignUp -> AuthScreenUiModel.Companion.AuthScreenState.SignIn
                        }
                    )
                }

            }

            is AuthScreenEvents.OnUsernameChange -> {
                _authScreenState.update { it.copy(username = event.username) }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signIn(email, password)
            onLoginSuccess()
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.signUp(username, email, password).fold(
                onSuccess = { onLoginSuccess() },
                onFailure = {
                    Log.e("SUPABASE AUTH", it.toString())
                }
            )

        }
    }

    fun onLoginSuccess() {
        navigator.navigateTo(route = "dashboard", popUpTo = "auth", inclusive = true)
    }
}