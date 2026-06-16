package com.praktika.studentdiary.presentation.navigation

import com.praktika.studentdiary.R

sealed class BottomNavItem(val route: String, val title: String, val icon: Int) {
    object Dashboard : BottomNavItem("dashboard", "Дашборд", R.drawable.home)
    object Calendar : BottomNavItem("calendar", "Календарь", R.drawable.calendar)
    object Materials : BottomNavItem("materials", "Материалы", R.drawable.list)
    object Simulator : BottomNavItem("simulator", "Тренажер", R.drawable.goal)
}