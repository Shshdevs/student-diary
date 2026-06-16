package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.SubjectDto
import com.praktika.studentdiary.data.dto.TaskDto
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class ScheduleSourceImpl @Inject constructor(
    private val postgrest: Postgrest,
) : ScheduleSource {

    override suspend fun getSubjects(userId: String): List<SubjectDto> {
        return postgrest["subjects"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<SubjectDto>()
    }

    override suspend fun addSubject(subject: SubjectDto): SubjectDto {
        return postgrest["subjects"]
            .insert(subject) { select() }
            .decodeSingle<SubjectDto>()
    }

    override suspend fun deleteSubject(subjectId: String) {
        postgrest["subjects"].delete {
            filter { eq("id", subjectId) }
        }
    }

    override suspend fun getTasks(userId: String): List<TaskDto> {
        return postgrest["tasks"]
            .select { filter { eq("user_id", userId) } }
            .decodeList<TaskDto>()
    }

    override suspend fun addTask(task: TaskDto): TaskDto {
        return postgrest["tasks"]
            .insert(task) { select() }
            .decodeSingle<TaskDto>()
    }

    override suspend fun updateTaskStatus(taskId: String, newStatus: String) {
        postgrest["tasks"].update(
            { set("status", newStatus) }
        ) {
            filter { eq("id", taskId) }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        postgrest["tasks"].delete {
            filter { eq("id", taskId) }
        }
    }
}