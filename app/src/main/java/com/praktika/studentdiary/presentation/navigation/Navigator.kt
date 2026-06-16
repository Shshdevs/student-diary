package com.praktika.studentdiary.presentation.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class NavigationIntent {
    data class NavigateTo(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false,
        val launchSingleTop: Boolean = false,
        val restoreState: Boolean = false,
        val saveState: Boolean = false
    ) : NavigationIntent()

    object NavigateBack : NavigationIntent()
}

@Singleton
class Navigator @Inject constructor() {
    private val _navigationEvents = MutableSharedFlow<NavigationIntent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun navigateTo(
        route: String,
        popUpTo: String? = null,
        inclusive: Boolean = false,
        launchSingleTop: Boolean = false,
        restoreState: Boolean = false,
        saveState: Boolean = false
    ) {
        _navigationEvents.tryEmit(
            NavigationIntent.NavigateTo(
                route = route,
                popUpTo = popUpTo,
                inclusive = inclusive,
                launchSingleTop = launchSingleTop,
                restoreState = restoreState,
                saveState = saveState
            )
        )
    }

    fun navigateBack() {
        _navigationEvents.tryEmit(NavigationIntent.NavigateBack)
    }
}