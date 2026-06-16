package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.praktika.studentdiary.presentation.navigation.BottomNavItem
import com.praktika.studentdiary.presentation.navigation.Navigator

@Composable
fun BottomNavBar(
    navController: NavController,
    navigator: Navigator,
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Calendar,
        BottomNavItem.Materials,
        BottomNavItem.Simulator
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navigator.navigateTo(
                            route = item.route,
                            popUpTo = BottomNavItem.Dashboard.route,
                            saveState = true,
                            launchSingleTop = true,
                            restoreState = true
                        )
                    }
                }
            )
        }
    }
}