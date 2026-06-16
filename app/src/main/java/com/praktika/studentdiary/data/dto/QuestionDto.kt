package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class QuestionDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("test_id") val testId: String,
    @SerialName("question_text") val questionText: String,
    @SerialName("question_type") val questionType: String,
    @SerialName("difficulty_level") val difficultyLevel: Int,
)