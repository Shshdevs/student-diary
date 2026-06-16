package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.SubjectDto
import com.praktika.studentdiary.data.dto.TaskDto
import com.praktika.studentdiary.data.dto.TestAttemptDto
import com.praktika.studentdiary.data.dto.TimeLogDto

interface DashboardSource {
    suspend fun getPendingExams(userId: String): List<TaskDto>
    suspend fun getMissedDeadlines(userId: String): List<TaskDto>
    suspend fun getTestAttempts(userId: String): List<TestAttemptDto>
    suspend fun getTimeLogs(userId: String): List<TimeLogDto>
    suspend fun getSubjects(userId: String): List<SubjectDto>
    suspend fun insertTimeLog(timeLog: TimeLogDto)
}