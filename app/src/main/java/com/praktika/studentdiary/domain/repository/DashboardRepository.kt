package com.praktika.studentdiary.domain.repository

import com.praktika.studentdiary.domain.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboardData(userId: String): Result<DashboardData>
}