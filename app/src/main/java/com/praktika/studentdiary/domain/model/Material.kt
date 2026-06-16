package com.praktika.studentdiary.domain.model

data class Material(
    val id: String,
    val title: String,
    val summary: String?,
    val glossary: List<GlossaryItem>,
)

