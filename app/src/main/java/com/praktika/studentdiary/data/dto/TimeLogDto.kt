package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeLogDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("time_spent_minutes") val timeSpentMinutes: Int,
)