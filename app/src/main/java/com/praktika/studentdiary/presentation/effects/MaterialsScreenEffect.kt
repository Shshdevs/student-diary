package com.praktika.studentdiary.presentation.effects

sealed class MaterialsScreenEffect {
    data class RequestExport(val content: String, val fileName: String) : MaterialsScreenEffect()
}