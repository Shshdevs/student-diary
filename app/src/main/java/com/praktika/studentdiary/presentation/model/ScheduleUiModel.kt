package com.praktika.studentdiary.presentation.model

import com.praktika.studentdiary.domain.model.Subject
import com.praktika.studentdiary.domain.model.Task

data class ScheduleUiModel(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val error: String? = null,

    val showAddTaskDialog: Boolean = false,
    val showAddSubjectDialog: Boolean = false,
)