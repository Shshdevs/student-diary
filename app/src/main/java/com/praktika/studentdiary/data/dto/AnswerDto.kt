package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AnswerDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("question_id") val questionId: String,
    @SerialName("answer_text") val answerText: String,
    @SerialName("is_correct") val isCorrect: Boolean,
)