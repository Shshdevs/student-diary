package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestAttemptDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("score_percent") val scorePercent: Float,
    @SerialName("attempt_date") val attemptDate: String
)