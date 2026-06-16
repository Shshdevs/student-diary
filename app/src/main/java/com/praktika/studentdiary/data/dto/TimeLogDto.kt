package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TimeLogDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("time_spent_minutes") val timeSpentMinutes: Int,
    @SerialName("log_date") val logDate: String,
)