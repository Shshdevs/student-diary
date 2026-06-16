package com.praktika.studentdiary

import com.praktika.studentdiary.data.source.AiSourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Test

class AiSourceImplIntegrationTest {

    @Test
    fun `generateSummaryAndGlossary makes real call to Mistral API`(): Unit = runBlocking {

        val apiKey = BuildConfig.MISTRAL_API_KEY

        assumeTrue(
            "MISTRAL_API_KEY не найден в переменных окружения",
            apiKey != null && apiKey.isNotBlank(),
        )

        val realHttpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
        }

        val aiSource = AiSourceImpl(realHttpClient)

        val testText = """
            Объектно-ориентированное программирование (ООП) — это парадигма разработки программного обеспечения, в основе которой лежит понятие «объекта». В отличие от процедурного программирования, где программа представляет собой последовательность функций и инструкций, в ООП программа строится как совокупность взаимодействующих объектов.
            
            Базовыми понятиями ООП являются Класс и Объект. 
            Класс — это абстрактный тип данных, своеобразный шаблон или чертеж, описывающий структуру и поведение определенной сущности. 
            Объект — это конкретный экземпляр класса, который создается в оперативной памяти программы. Он обладает собственным состоянием (которое хранится в полях или атрибутах) и поведением (которое реализуется через методы).
            
            Фундамент парадигмы ООП держится на четырех главных принципах:
            
            1. Абстракция — это процесс выделения значимых характеристик предмета и отбрасывания несущественных деталей. Абстракция позволяет управлять сложностью системы, создавая упрощенную модель реального объекта. Разработчик концентрируется на том, что объект делает, а не на том, как он устроен внутри.
            
            2. Инкапсуляция — это механизм объединения данных и методов, работающих с этими данными, в единый компонент (класс). Важной частью инкапсуляции является сокрытие внутреннего состояния объекта от прямого вмешательства извне. Доступ к данным обычно предоставляется исключительно через публичный интерфейс (специальные методы, такие как геттеры и сеттеры).
            
            3. Наследование — это свойство системы, позволяющее описать новый класс на основе уже существующего родительского класса (суперкласса). Класс-наследник (дочерний класс) автоматически получает все свойства и методы родителя, а также может добавлять свои собственные. Это мощный инструмент, который избавляет от дублирования кода.
            
            4. Полиморфизм — это способность программы использовать объекты с одинаковым интерфейсом без информации о конкретном типе этого объекта. Простыми словами, это свойство позволяет функции или методу обрабатывать данные разных типов. Чаще всего полиморфизм выражается в переопределении методов: разные классы-наследники могут по-разному реализовывать один и тот же метод родительского класса.
            
            Использование ООП дает ряд существенных преимуществ при промышленной разработке. Код становится более модульным и читаемым, его легче тестировать, поддерживать и масштабировать при увеличении сложности проекта.
        """.trimIndent()
        val result = try {
            val result = aiSource.generateTest("ООП", testText)
            result
        } catch (e: Exception) {
            println()
            fail("Сетевой запрос или парсинг завершился с ошибкой: ${e}")
            return@runBlocking
        }

    }
}