package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.BuildConfig
import com.praktika.studentdiary.data.dto.AiGenerationResultDto
import com.praktika.studentdiary.data.dto.MistralMessage
import com.praktika.studentdiary.data.dto.MistralRequest
import com.praktika.studentdiary.data.dto.MistralResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
) : AiSource {

    override suspend fun generateSummaryAndGlossary(text: String): AiGenerationResultDto {
        val prompt = """
            Проанализируй следующий учебный текст. 
            Верни ответ СТРОГО в формате чистого JSON.
            ВАЖНО: Твой ответ должен начинаться с символа '{' и заканчиваться символом '}'. 
            Никакого дополнительного текста, никаких пояснений и никакой markdown-разметки! 
            
            Ожидаемая структура JSON:
            {
              "summary": "строка с конспектом. Используй переносы строк (\n) для разделения на абзацы и пункты.",
              "glossary": [
                {
                  "term": "название термина",
                  "definition": "определение термина"
                }
              ]
            }
            
            Текст лекции:
            $text
        """.trimIndent()

        val requestBody = MistralRequest(
            model = "mistral-large-latest",
            messages = listOf(MistralMessage(role = "user", content = prompt))
        )

        val httpResponse: HttpResponse =
            httpClient.post("https://api.mistral.ai/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.MISTRAL_API_KEY}")
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(requestBody)
            }

        if (!httpResponse.status.isSuccess()) {
            val errorBody = httpResponse.bodyAsText()
            throw Exception("Mistral API error (${httpResponse.status}): $errorBody")
        }

        val response: MistralResponse = httpResponse.body()

        val rawContent = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("AI returned empty response")

        val jsonString = rawContent
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        return Json { ignoreUnknownKeys = true }.decodeFromString(jsonString)
    }
}