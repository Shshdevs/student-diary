package com.praktika.studentdiary.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.praktika.studentdiary.presentation.ui.screen.AuthScreen
import com.praktika.studentdiary.presentation.ui.screen.DashboardScreen
import com.praktika.studentdiary.presentation.ui.screen.MaterialsScreen
import com.praktika.studentdiary.presentation.ui.screen.ScheduleScreen
import com.praktika.studentdiary.presentation.ui.screen.SimulatorScreen

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
        startDestination = "auth_graph",
        modifier = Modifier.padding(paddingValues)
    ) {
        navigation(
            route = "auth_graph",
            startDestination = "auth"
        ) {
            composable("auth") {
                AuthScreen()
            }
        }

        navigation(
            route = "dashboard_graph",
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                DashboardScreen()
            }
        }

        navigation(
            route = "calendar_graph",
            startDestination = "calendar"
        ) {
            composable("calendar") {
                ScheduleScreen()
            }
        }

        navigation(
            route = "materials_graph",
            startDestination = "materials"
        ) {
            composable("materials") {
                MaterialsScreen()
            }
        }

        navigation(
            route = "simulator_graph",
            startDestination = "simulator?materialId={materialId}"
        ) {
            composable(
                route = "simulator?materialId={materialId}",
                arguments = listOf(
                    navArgument("materialId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val materialId = backStackEntry.arguments?.getString("materialId")

                 SimulatorScreen(materialId = materialId)
            }
        }


    }
}