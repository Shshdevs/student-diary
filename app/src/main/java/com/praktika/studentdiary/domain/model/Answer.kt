package com.praktika.studentdiary.domain.model

data class Answer(
    val id: String,
    val questionId: String,
    val answerText: String,
    val isCorrect: Boolean
)