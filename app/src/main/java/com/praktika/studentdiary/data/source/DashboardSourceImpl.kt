package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.SubjectDto
import com.praktika.studentdiary.data.dto.TaskDto
import com.praktika.studentdiary.data.dto.TestAttemptDto
import com.praktika.studentdiary.data.dto.TimeLogDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DashboardSourceImpl @Inject constructor(
    private val postgrest: Postgrest,
) : DashboardSource {

    override suspend fun getPendingExams(userId: String): List<TaskDto> {
        return postgrest["tasks"].select {
            filter {
                eq("user_id", userId)
                eq("task_type", "exam")
                eq("status", "pending")
            }
        }.decodeList<TaskDto>()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getMissedDeadlines(userId: String): List<TaskDto> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return postgrest["tasks"].select {
            filter {
                eq("user_id", userId)
                eq("status", "pending")
                lt("due_date", now.toString())
            }
        }.decodeList<TaskDto>()
    }

    override suspend fun getTestAttempts(userId: String): List<TestAttemptDto> {
        return postgrest["test_attempts"].select {
            filter { eq("user_id", userId) }
        }.decodeList<TestAttemptDto>()
    }

    override suspend fun getTimeLogs(userId: String): List<TimeLogDto> {
        return postgrest["time_logs"].select {
            filter { eq("user_id", userId) }
        }.decodeList<TimeLogDto>()
    }

    override suspend fun getSubjects(userId: String): List<SubjectDto> {
        return postgrest["subjects"].select {
            filter { eq("user_id", userId) }
        }.decodeList<SubjectDto>()
    }
}