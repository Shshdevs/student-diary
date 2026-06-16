package com.praktika.studentdiary.presentation.model

import com.praktika.studentdiary.domain.model.Material
import com.praktika.studentdiary.domain.model.Subject

data class MaterialsScreenUiModel(
    val isLoading: Boolean = false,
    val isGeneratingAi: Boolean = false,
    val subjects: List<Subject> = emptyList(),
    val selectedSubjectId: String? = null,
    val materials: List<Material> = emptyList(),
    val selectedMaterial: Material? = null,
    val error: String? = null,
)