package com.praktika.studentdiary.data.source

import com.praktika.studentdiary.data.dto.GlossaryDto
import com.praktika.studentdiary.data.dto.MaterialDto

interface MaterialsSource {
    suspend fun getMaterials(subjectId: String): List<MaterialDto>
    suspend fun getGlossary(materialId: String): List<GlossaryDto>
    suspend fun saveMaterial(material: MaterialDto): MaterialDto
    suspend fun saveGlossary(items: List<GlossaryDto>)
    suspend fun deleteMaterial(materialId: String)
}