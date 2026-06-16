package com.praktika.studentdiary.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.praktika.studentdiary.presentation.events.AuthScreenEvents
import com.praktika.studentdiary.presentation.model.AuthScreenUiModel
import com.praktika.studentdiary.presentation.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val authState by viewModel.sessionState.collectAsState(SessionStatus.Initializing)
    when (authState) {
        is SessionStatus.Authenticated -> {
            viewModel.onLoginSuccess()
        }

        SessionStatus.Initializing -> {}
        is SessionStatus.NotAuthenticated -> {
            val uiModel by viewModel.authScreenState.collectAsState()
            AuthScreenContent(uiModel, viewModel::onEvent)
        }

        is SessionStatus.RefreshFailure -> {}
    }
}

@Composable
fun AuthScreenContent(
    uiModel: AuthScreenUiModel,
    onEvent: (AuthScreenEvents) -> Unit,
) {
    val isSignIn = uiModel.authScreenState == AuthScreenUiModel.Companion.AuthScreenState.SignIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSignIn) "Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!isSignIn) {
            OutlinedTextField(
                value = uiModel.username,
                onValueChange = { onEvent(AuthScreenEvents.OnUsernameChange(it)) },
                label = { Text("Имя пользователя") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = uiModel.email,
            onValueChange = { onEvent(AuthScreenEvents.OnEmailChange(it)) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiModel.password,
            onValueChange = { onEvent(AuthScreenEvents.OnPasswordChange(it)) },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                if (isSignIn) {
                    onEvent(AuthScreenEvents.OnSignIn)
                } else {
                    onEvent(AuthScreenEvents.OnSignUp)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(if (isSignIn) "Войти" else "Зарегистрироваться")
        }

        TextButton(
            onClick = { onEvent(AuthScreenEvents.OnToggleState) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSignIn) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войти"
            )
        }
    }
}