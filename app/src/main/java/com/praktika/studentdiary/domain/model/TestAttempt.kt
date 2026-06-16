package com.praktika.studentdiary.domain.model

data class TestAttempt(
    val id: String,
    val testId: String,
    val userId: String,
    val scorePercent: Double,
    val attemptDate: String?,
)