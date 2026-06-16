package com.praktika.studentdiary.presentation.events

sealed class AuthScreenEvents {
    data class OnUsernameChange(val username: String) : AuthScreenEvents()
    data class OnEmailChange(val email: String) : AuthScreenEvents()
    data class OnPasswordChange(val password: String) : AuthScreenEvents()

    object OnSignIn : AuthScreenEvents()
    object OnSignUp : AuthScreenEvents()
    object OnToggleState : AuthScreenEvents()
}