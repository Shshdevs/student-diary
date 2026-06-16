package com.praktika.studentdiary.domain.repository

import com.praktika.studentdiary.domain.model.Test
import com.praktika.studentdiary.domain.model.TestAttempt

interface TestRepository {
    suspend fun getTestForMaterial(materialId: String): Result<Test?>
    suspend fun generateAndSaveTest(
        materialId: String,
        materialTitle: String,
        rawText: String,
    ): Result<Test>

    suspend fun saveTestAttempt(
        testId: String,
        userId: String,
        scorePercent: Double,
    ): Result<TestAttempt>

    suspend fun getAttemptsHistory(testId: String, userId: String): Result<List<TestAttempt>>
}