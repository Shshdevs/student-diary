package com.praktika.studentdiary.domain.model

data class Test(
    val id: String,
    val materialId: String,
    val title: String,
    val questions: List<Question> = emptyList(),
)