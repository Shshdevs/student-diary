package com.praktika.studentdiary.presentation.model

data class AuthScreenUiModel(
    val authScreenState: AuthScreenState = AuthScreenState.SignIn,
    val username: String = "",
    val email: String = "",
    val password: String = "",
) {
    companion object {
        sealed class AuthScreenState {
            object SignUp : AuthScreenState()
            object SignIn : AuthScreenState()
        }
    }
}