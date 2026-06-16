package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.AiGenerationResultDto
import com.praktika.studentdiary.data.dto.AiTestDto

interface AiSource {
    suspend fun generateSummaryAndGlossary(text: String): AiGenerationResultDto
    suspend fun generateTest(materialTitle: String, text: String): AiTestDto
}