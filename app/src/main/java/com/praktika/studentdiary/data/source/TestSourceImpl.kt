package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.AnswerDto
import com.praktika.studentdiary.data.dto.QuestionDto
import com.praktika.studentdiary.data.dto.TestAttemptDto
import com.praktika.studentdiary.data.dto.TestDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class TestSourceImpl @Inject constructor(
    private val postgres: Postgrest,
) : TestSource {

    override suspend fun getTestByMaterial(materialId: String): TestDto? {
        return postgres["tests"]
            .select { filter { eq("material_id", materialId) } }
            .decodeList<TestDto>()
            .firstOrNull()
    }

    override suspend fun getQuestions(testId: String): List<QuestionDto> {
        return postgres["questions"]
            .select { filter { eq("test_id", testId) } }
            .decodeList()
    }

    override suspend fun getAnswers(questionId: String): List<AnswerDto> {
        return postgres["answers"]
            .select { filter { eq("question_id", questionId) } }
            .decodeList()
    }

    override suspend fun saveTest(test: TestDto): TestDto {
        return postgres["tests"]
            .insert(test) { select() }
            .decodeSingle()
    }

    override suspend fun saveQuestions(questions: List<QuestionDto>): List<QuestionDto> {
        return postgres["questions"]
            .insert(questions) { select() }
            .decodeList()
    }

    override suspend fun saveAnswers(answers: List<AnswerDto>) {
        if (answers.isEmpty()) return
        postgres["answers"].insert(answers)
    }

    override suspend fun saveAttempt(attempt: TestAttemptDto): TestAttemptDto {
        return postgres["test_attempts"]
            .insert(attempt) { select() }
            .decodeSingle()
    }

    override suspend fun getAttempts(testId: String, userId: String): List<TestAttemptDto> {
        return postgres["test_attempts"]
            .select {
                filter { eq("test_id", testId); eq("user_id", userId) }
                order("attempt_date", Order.ASCENDING)
            }
            .decodeList()
    }
}