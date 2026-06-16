package com.praktika.studentdiary.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiTestDto(
    val title: String,
    val questions: List<AiQuestionDto>,
) {
    @Serializable
    data class AiQuestionDto(
        val questionText: String,
        val questionType: String,
        val difficultyLevel: Int,
        val answers: List<AiAnswerDto>,
    )

    @Serializable
    data class AiAnswerDto(
        val answerText: String,
        val isCorrect: Boolean,
    )
}