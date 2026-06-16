package com.praktika.studentdiary.data.repository

import com.praktika.studentdiary.data.dto.GlossaryDto
import com.praktika.studentdiary.data.dto.MaterialDto
import com.praktika.studentdiary.data.source.AiSource
import com.praktika.studentdiary.data.source.MaterialsSource
import com.praktika.studentdiary.domain.model.GlossaryItem
import com.praktika.studentdiary.domain.model.Material
import com.praktika.studentdiary.domain.repository.MaterialsRepository
import javax.inject.Inject

class MaterialsRepositoryImpl @Inject constructor(
    private val materialsSource: MaterialsSource,
    private val aiSource: AiSource,
) : MaterialsRepository {

    override suspend fun getMaterialsForSubject(subjectId: String): Result<List<Material>> {
        return try {
            val dtos = materialsSource.getMaterials(subjectId)
            val materials = dtos.map { dto ->
                val glossaryDtos = materialsSource.getGlossary(dto.id)
                Material(
                    id = dto.id,
                    title = dto.title,
                    summary = dto.summaryText,
                    glossary = glossaryDtos.map { GlossaryItem(it.term, it.definition) }
                )
            }
            Result.success(materials)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun processAndSaveNewMaterial(
        userId: String,
        subjectId: String,
        title: String,
        rawText: String,
    ): Result<Material> {
        return try {
            val aiResult = aiSource.generateSummaryAndGlossary(rawText)

            val newMaterialDto = MaterialDto(
                userId = userId,
                subjectId = subjectId,
                title = title,
                originalText = rawText,
                summaryText = aiResult.summary
            )
            val savedMaterial = materialsSource.saveMaterial(newMaterialDto)

            val glossaryDtos = aiResult.glossary.map { term ->
                GlossaryDto(
                    materialId = savedMaterial.id,
                    term = term.term,
                    definition = term.definition
                )
            }
            materialsSource.saveGlossary(glossaryDtos)

            Result.success(
                Material(
                    id = savedMaterial.id,
                    title = savedMaterial.title,
                    summary = savedMaterial.summaryText,
                    glossary = aiResult.glossary.map { GlossaryItem(it.term, it.definition) }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMaterial(materialId: String): Result<Unit> {
        return try {
            materialsSource.deleteMaterial(materialId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}