package com.praktika.studentdiary.data.repository

import com.praktika.studentdiary.data.dto.TimeLogDto
import com.praktika.studentdiary.data.source.DashboardSource
import com.praktika.studentdiary.domain.model.DashboardData
import com.praktika.studentdiary.domain.repository.DashboardRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val source: DashboardSource,
) : DashboardRepository {

    override suspend fun getDashboardData(userId: String): Result<DashboardData> {
        return try {
            val exams = source.getPendingExams(userId)
            val missedDeadlines = source.getMissedDeadlines(userId)
            val tests = source.getTestAttempts(userId)
            val timeLogs = source.getTimeLogs(userId)
            val subjects = source.getSubjects(userId)

            val subjectMap = subjects.associateBy { it.id }

            val nearestExamDate = exams.minOfOrNull {
                LocalDateTime.parse(it.dueDate)
            }

            val completedTestsCount = tests.size
            val averageTestScore = if (completedTestsCount > 0) {
                tests.map { it.scorePercent }.average().toFloat()
            } else 0f

            val timeDistribution = timeLogs
                .groupBy { it.subjectId }
                .mapKeys { subjectMap[it.key]?.name ?: "Неизвестно" }
                .mapValues { entry -> entry.value.sumOf { it.timeSpentMinutes } }


            val subjectsProgress = subjects.associate { subject ->
                subject.name to 0f
            }

            val dashboardData = DashboardData(
                nearestExamDate = nearestExamDate,
                missedDeadlinesCount = missedDeadlines.size,
                averageTestScore = averageTestScore,
                completedTestsCount = completedTestsCount,
                timeDistribution = timeDistribution,
                subjectsProgress = subjectsProgress
            )

            Result.success(dashboardData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logTime(
        userId: String,
        subjectId: String,
        timeSpentMinutes: Int,
    ): Result<Unit> {
        return try {
            val today =
                LocalDate.now().toString()

            val timeLog = TimeLogDto(
                userId = userId,
                subjectId = subjectId,
                timeSpentMinutes = timeSpentMinutes,
                logDate = today
            )

            source.insertTimeLog(timeLog)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}