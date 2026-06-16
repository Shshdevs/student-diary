package com.praktika.studentdiary.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiGenerationResultDto(
    val summary: String,
    val glossary: List<GlossaryTermDto>,
) {
    @Serializable
    data class GlossaryTermDto(val term: String, val definition: String)
}