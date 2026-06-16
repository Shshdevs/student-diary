package com.praktika.studentdiary.data.source

import android.util.Log
import com.praktika.studentdiary.data.dto.GlossaryDto
import com.praktika.studentdiary.data.dto.MaterialDto
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject


class MaterialsSourceImpl @Inject constructor(
    private val postgres: Postgrest,
) : MaterialsSource {

    override suspend fun getMaterialById(materialId: String): MaterialDto {
        return postgres["materials"]
            .select { filter { eq("id", materialId); eq("is_deleted", false) } }
            .decodeSingle()
    }

    override suspend fun getMaterials(subjectId: String): List<MaterialDto> {
        return postgres["materials"]
            .select { filter { eq("subject_id", subjectId); eq("is_deleted", false) } }
            .decodeList()
    }

    override suspend fun getGlossary(materialId: String): List<GlossaryDto> {
        return postgres["glossary"]
            .select { filter { eq("material_id", materialId) } }
            .decodeList()
    }

    override suspend fun saveMaterial(material: MaterialDto): MaterialDto {
        Log.d("Saving material", material.toString())
        return postgres["materials"]
            .insert(material) { select() }
            .decodeSingle()
    }

    override suspend fun saveGlossary(items: List<GlossaryDto>) {
        if (items.isEmpty()) return
        postgres["glossary"].insert(items)
    }

    override suspend fun deleteMaterial(materialId: String) {
        postgres["materials"]
            .update({ set("is_deleted", true) }) {
                filter { eq("id", materialId) }
            }
    }
}