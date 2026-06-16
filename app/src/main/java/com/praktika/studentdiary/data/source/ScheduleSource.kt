package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.SubjectDto
import com.praktika.studentdiary.data.dto.TaskDto

interface ScheduleSource {
    suspend fun getSubjects(userId: String): List<SubjectDto>
    suspend fun addSubject(subject: SubjectDto): SubjectDto
    suspend fun deleteSubject(subjectId: String)
    suspend fun getTasks(userId: String): List<TaskDto>
    suspend fun addTask(task: TaskDto): TaskDto
    suspend fun updateTaskStatus(taskId: String, newStatus: String)
    suspend fun deleteTask(taskId: String)
}