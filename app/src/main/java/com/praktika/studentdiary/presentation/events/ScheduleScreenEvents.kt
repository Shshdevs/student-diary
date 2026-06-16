package com.praktika.studentdiary.presentation.events

import java.time.LocalDateTime


sealed class ScheduleScreenEvents {
    object LoadData : ScheduleScreenEvents()

    data class CreateSubject(val name: String) : ScheduleScreenEvents()
    data class DeleteSubject(val subjectId: String) : ScheduleScreenEvents()

    data class CreateTask(
        val subjectId: String,
        val title: String,
        val type: String,
        val dueDate: LocalDateTime,
    ) : ScheduleScreenEvents()

    data class CompleteTask(val taskId: String) : ScheduleScreenEvents()

    data class OnShowAddTaskDialog(val show: Boolean) : ScheduleScreenEvents()
    data class OnShowAddSubjectDialog(val show: Boolean) : ScheduleScreenEvents()
}