package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("subject_id") val subjectId: String,
    val title: String,
    @SerialName("task_type") val taskType: String,
    @SerialName("due_date") val dueDate: String,
    @SerialName("reminder_time") val reminderTime: String? = null,
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String? = null
)