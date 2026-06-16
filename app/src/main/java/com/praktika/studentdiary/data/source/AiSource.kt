package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.AiGenerationResultDto

interface AiSource {
    suspend fun generateSummaryAndGlossary(text: String): AiGenerationResultDto
}