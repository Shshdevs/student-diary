package com.praktika.studentdiary.domain.repository

import com.praktika.studentdiary.domain.model.Material

interface MaterialsRepository {
    suspend fun getMaterialsForSubject(subjectId: String): Result<List<Material>>
    suspend fun processAndSaveNewMaterial(userId: String, subjectId: String, title: String, rawText: String): Result<Material>
    suspend fun deleteMaterial(materialId: String): Result<Unit>
}