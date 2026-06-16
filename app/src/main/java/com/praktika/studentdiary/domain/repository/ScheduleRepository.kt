package com.praktika.studentdiary.domain.repository

import com.praktika.studentdiary.domain.model.Subject
import com.praktika.studentdiary.domain.model.Task
import java.time.LocalDateTime

interface ScheduleRepository {
    suspend fun fetchSubjects(userId: String): Result<List<Subject>>
    suspend fun createSubject(userId: String, name: String): Result<Subject>
    suspend fun deleteSubject(subjectId: String): Result<Unit>

    suspend fun fetchTasks(userId: String): Result<List<Task>>
    suspend fun createTask(
        userId: String,
        subjectId: String,
        title: String,
        type: String,
        date: LocalDateTime,
    ): Result<Unit>

    suspend fun completeTask(taskId: String): Result<Unit>
}