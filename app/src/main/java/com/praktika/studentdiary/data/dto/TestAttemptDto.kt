package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TestAttemptDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("test_id") val testId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score_percent") val scorePercent: Double,
    @SerialName("attempt_date") val attemptDate: String? = null,
)