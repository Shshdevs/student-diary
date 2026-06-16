package com.praktika.studentdiary.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.praktika.studentdiary.presentation.navigation.NavHostContainer
import com.praktika.studentdiary.presentation.navigation.Navigator
import com.praktika.studentdiary.presentation.ui.components.BottomNavBar

@Composable
fun App(navigator: Navigator) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (navController.currentBackStackEntry?.destination?.route != "auth") {
                BottomNavBar(
                    navController = navController,
                    navigator = navigator
                )
            }
        }) { innerPadding ->
        NavHostContainer(
            navigator = navigator,
            paddingValues = innerPadding
        )
    }
}