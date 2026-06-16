package com.praktika.studentdiary.data.source

import android.util.Log
import com.praktika.studentdiary.BuildConfig
import com.praktika.studentdiary.data.dto.AiGenerationResultDto
import com.praktika.studentdiary.data.dto.AiTestDto
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
            НЕ ИСПОЛЬЗУЙ MarkDown разметку в тексте!
            
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

        Log.d("Ktor response", httpResponse.toString())
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

    override suspend fun generateTest(materialTitle: String, text: String): AiTestDto {
        val prompt = """
        На основе следующего учебного текста лекции создай проверочный тест.
        Сгенерируй НЕ МЕНЕЕ 5 качественных вопросов разного формата.
        
        Форматы вопросов (поле questionType):
        1. 'CHOICE' — вопрос с несколькими вариантами ответов (один из них правильный).
        2. 'OPEN' — открытый вопрос, где правильный ответ пишется текстом (в массиве answers укажи один правильный вариант).
        3. 'TRUE_FALSE' — утверждение "верно/неверно" (в массиве answers должно быть ровно два варианта: "Верно" и "Неверно" с указанием корректности).
        
        Распредели уровни сложности (difficultyLevel) от 1 до 5 в зависимости от глубины вопроса.
        
        Верни ответ СТРОГО в формате чистого JSON. Твой ответ должен начинаться с символа '{' и заканчиваться символом '}'. Никакого дополнительного текста и markdown-разметки!
        НЕ ИСПОЛЬЗУЙ MarkDown разметку в тексте!

        Ожидаемая структура JSON, которой необходимо следовать:
        {
          "title": "Тест по теме: $materialTitle",
          "questions": [
            {
              "questionText": "Текст вопроса",
              "questionType": "CHOICE",
              "difficultyLevel": 2,
              "answers": [
                { "answerText": "Вариант ответа", "isCorrect": true },
                { "answerText": "Другой вариант", "isCorrect": false }
              ]
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
            ?: throw Exception("AI returned empty test response")

        val jsonString = rawContent
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return Json { ignoreUnknownKeys = true }.decodeFromString(jsonString)
    }
}