package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.AnswerDto
import com.praktika.studentdiary.data.dto.QuestionDto
import com.praktika.studentdiary.data.dto.TestAttemptDto
import com.praktika.studentdiary.data.dto.TestDto

interface TestSource {
    suspend fun getTestByMaterial(materialId: String): TestDto?
    suspend fun getQuestions(testId: String): List<QuestionDto>
    suspend fun getAnswers(questionId: String): List<AnswerDto>

    suspend fun saveTest(test: TestDto): TestDto
    suspend fun saveQuestions(questions: List<QuestionDto>): List<QuestionDto>
    suspend fun saveAnswers(answers: List<AnswerDto>)

    suspend fun saveAttempt(attempt: TestAttemptDto): TestAttemptDto
    suspend fun getAttempts(testId: String, userId: String): List<TestAttemptDto>
}