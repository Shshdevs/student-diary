package com.praktika.studentdiary.data.repository

import com.praktika.studentdiary.data.dto.SubjectDto
import com.praktika.studentdiary.data.dto.TaskDto
import com.praktika.studentdiary.data.source.ScheduleSource
import com.praktika.studentdiary.domain.model.Subject
import com.praktika.studentdiary.domain.model.Task
import com.praktika.studentdiary.domain.repository.ScheduleRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val remoteSource: ScheduleSource,
) : ScheduleRepository {

    override suspend fun fetchSubjects(userId: String): Result<List<Subject>> {
        return try {
            val dtos = remoteSource.getSubjects(userId)
            Result.success(dtos.map { Subject(id = it.id!!, name = it.name) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSubject(userId: String, name: String): Result<Subject> {
        return try {
            val newSubject = SubjectDto(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = name
            )
            val inserted = remoteSource.addSubject(newSubject)
            Result.success(Subject(id = inserted.id!!, name = inserted.name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSubject(subjectId: String): Result<Unit> {
        return try {
            remoteSource.deleteSubject(subjectId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchTasks(userId: String): Result<List<Task>> {
        return try {
            val dtos = remoteSource.getTasks(userId)
            val domainTasks = dtos.map { dto ->
                Task(
                    id = dto.id!!,
                    subjectId = dto.subjectId,
                    title = dto.title,
                    dueDate = LocalDateTime.parse(dto.dueDate),
                    taskType = dto.taskType,
                    status = dto.status
                )
            }.sortedBy { it.dueDate }

            Result.success(domainTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTask(
        userId: String,
        subjectId: String,
        title: String,
        type: String,
        date: LocalDateTime,
    ): Result<Unit> {
        return try {
            val dto = TaskDto(
                id = UUID.randomUUID().toString(),
                userId = userId,
                subjectId = subjectId,
                title = title,
                taskType = type,
                dueDate = date.toString()
            )
            remoteSource.addTask(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(taskId: String): Result<Unit> {
        return try {
            remoteSource.updateTaskStatus(taskId, "completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}