package com.praktika.studentdiary.presentation.events

import android.net.Uri
import com.praktika.studentdiary.domain.model.Material

sealed class MaterialsScreenEvents {
    object LoadSubjects : MaterialsScreenEvents()
    data class SelectSubject(val subjectId: String) : MaterialsScreenEvents()
    data class SelectMaterial(val material: Material?) :
        MaterialsScreenEvents()

    data class GenerateMaterial(val title: String, val rawText: String) : MaterialsScreenEvents()
    data class DeleteMaterial(val materialId: String) : MaterialsScreenEvents()
    data class ImportPdf(val uri: Uri, val fileName: String) : MaterialsScreenEvents()
    object DismissError : MaterialsScreenEvents()
    data class GoToSimulator(val materialId: String) : MaterialsScreenEvents()
}
