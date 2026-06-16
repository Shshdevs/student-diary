package com.praktika.studentdiary.domain.model

import java.time.LocalDateTime

data class DashboardData(
    val nearestExamDate: LocalDateTime?,
    val missedDeadlinesCount: Int,
    val averageTestScore: Float,
    val completedTestsCount: Int,
    val timeDistribution: Map<String, Int>,
    val subjectsProgress: Map<String, Float>,
)