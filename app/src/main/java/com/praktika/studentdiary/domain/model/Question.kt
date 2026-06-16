package com.praktika.studentdiary.domain.model

data class Question(
    val id: String,
    val testId: String,
    val questionText: String,
    val questionType: QuestionType,
    val difficultyLevel: Int,
    val answers: List<Answer>,
)

enum class QuestionType {
    CHOICE,
    OPEN,
    TRUE_FALSE
}