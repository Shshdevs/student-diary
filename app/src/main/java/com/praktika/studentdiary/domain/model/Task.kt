package com.praktika.studentdiary.domain.model

import java.time.LocalDateTime

data class Task(
    val id: String,
    val subjectId: String,
    val title: String,
    val dueDate: LocalDateTime,
    val taskType: String,
    val status: String
)