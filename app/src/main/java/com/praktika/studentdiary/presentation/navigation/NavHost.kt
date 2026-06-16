package com.praktika.studentdiary.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.praktika.studentdiary.presentation.ui.screen.AuthScreen
import com.praktika.studentdiary.presentation.ui.screen.DashboardScreen
import com.praktika.studentdiary.presentation.ui.screen.MaterialsScreen
import com.praktika.studentdiary.presentation.ui.screen.ScheduleScreen

@Composable
fun NavHostContainer(
    navigator: Navigator,
    paddingValues: PaddingValues,
) {
    val navController = rememberNavController()
    LaunchedEffect(navigator) {
        navigator.navigationEvents.collect { intent ->
            when (intent) {
                is NavigationIntent.NavigateTo -> {
                    navController.navigate(intent.route) {
                        intent.popUpTo?.let { route ->
                            popUpTo(route) {
                                inclusive = intent.inclusive
                                saveState = intent.saveState
                            }
                        }
                        launchSingleTop = intent.launchSingleTop
                        restoreState = intent.restoreState
                    }
                }

                is NavigationIntent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("auth") {
            AuthScreen()
        }
        composable("dashboard") {
            DashboardScreen()
        }
        composable("calendar") {
            ScheduleScreen()
        }
        composable("materials") {
            MaterialsScreen()

        }
        composable("simulator") {

        }
    }
}