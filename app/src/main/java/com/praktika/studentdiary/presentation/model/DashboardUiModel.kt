package com.praktika.studentdiary.presentation.model

import com.praktika.studentdiary.domain.model.DashboardData

data class DashboardUiModel(
    val isLoading: Boolean = false,
    val data: DashboardData? = null,
    val error: String? = null,
)