package com.praktika.studentdiary.data.repository

import com.praktika.studentdiary.data.dto.AnswerDto
import com.praktika.studentdiary.data.dto.QuestionDto
import com.praktika.studentdiary.data.dto.TestAttemptDto
import com.praktika.studentdiary.data.dto.TestDto
import com.praktika.studentdiary.data.source.AiSource
import com.praktika.studentdiary.data.source.TestSource
import com.praktika.studentdiary.domain.model.Answer
import com.praktika.studentdiary.domain.model.Question
import com.praktika.studentdiary.domain.model.QuestionType
import com.praktika.studentdiary.domain.model.Test
import com.praktika.studentdiary.domain.model.TestAttempt
import com.praktika.studentdiary.domain.repository.TestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testSource: TestSource,
    private val aiSource: AiSource,
) : TestRepository {

    override suspend fun getTestForMaterial(materialId: String): Result<Test?> =
        withContext(Dispatchers.IO) {
            try {
                val testDto =
                    testSource.getTestByMaterial(materialId) ?: return@withContext Result.success(
                        null
                    )

                val questionDtos = testSource.getQuestions(testDto.id!!)
                val questions = questionDtos.map { qDto ->
                    val answerDtos = testSource.getAnswers(qDto.id!!)
                    Question(
                        id = qDto.id,
                        testId = qDto.testId,
                        questionText = qDto.questionText,
                        questionType = QuestionType.valueOf(qDto.questionType),
                        difficultyLevel = qDto.difficultyLevel,
                        answers = answerDtos.map {
                            Answer(
                                it.id!!,
                                it.questionId,
                                it.answerText,
                                it.isCorrect
                            )
                        }
                    )
                }

                Result.success(
                    Test(
                        id = testDto.id,
                        materialId = testDto.materialId,
                        title = testDto.title,
                        questions = questions
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun generateAndSaveTest(
        materialId: String,
        materialTitle: String,
        rawText: String,
    ): Result<Test> = withContext(Dispatchers.IO) {
        try {
            val aiTest = aiSource.generateTest(materialTitle, rawText)

            val savedTestDto = testSource.saveTest(
                TestDto(materialId = materialId, title = aiTest.title)
            )
            val testId =
                savedTestDto.id ?: throw Exception("Ошибка: база данных не вернула ID теста")

            val finalQuestions = mutableListOf<Question>()

            aiTest.questions.forEach { aiQuestion ->
                val savedQuestionDto = testSource.saveQuestions(
                    listOf(
                        QuestionDto(
                            testId = testId,
                            questionText = aiQuestion.questionText,
                            questionType = aiQuestion.questionType,
                            difficultyLevel = aiQuestion.difficultyLevel
                        )
                    )
                ).first()

                val questionId = savedQuestionDto.id!!

                val answerDtos = aiQuestion.answers.map { aiAnswer ->
                    AnswerDto(
                        questionId = questionId,
                        answerText = aiAnswer.answerText,
                        isCorrect = aiAnswer.isCorrect
                    )
                }
                testSource.saveAnswers(answerDtos)

                val savedAnswers = testSource.getAnswers(questionId).map {
                    Answer(it.id!!, it.questionId, it.answerText, it.isCorrect)
                }

                finalQuestions.add(
                    Question(
                        id = questionId,
                        testId = testId,
                        questionText = savedQuestionDto.questionText,
                        questionType = QuestionType.valueOf(savedQuestionDto.questionType),
                        difficultyLevel = savedQuestionDto.difficultyLevel,
                        answers = savedAnswers
                    )
                )
            }

            Result.success(
                Test(
                    id = testId,
                    materialId = materialId,
                    title = savedTestDto.title,
                    questions = finalQuestions
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveTestAttempt(
        testId: String,
        userId: String,
        scorePercent: Double,
    ): Result<TestAttempt> = withContext(Dispatchers.IO) {
        try {
            val dto = testSource.saveAttempt(
                TestAttemptDto(testId = testId, userId = userId, scorePercent = scorePercent)
            )
            Result.success(
                TestAttempt(
                    id = dto.id!!,
                    testId = dto.testId,
                    userId = dto.userId,
                    scorePercent = dto.scorePercent,
                    attemptDate = dto.attemptDate
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttemptsHistory(
        testId: String,
        userId: String,
    ): Result<List<TestAttempt>> = withContext(Dispatchers.IO) {
        try {
            val dtos = testSource.getAttempts(testId, userId)
            val models = dtos.map {
                TestAttempt(
                    id = it.id!!,
                    testId = it.testId,
                    userId = it.userId,
                    scorePercent = it.scorePercent,
                    attemptDate = it.attemptDate
                )
            }
            Result.success(models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}