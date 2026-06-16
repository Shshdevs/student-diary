package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestDto(
    @SerialName("id") val id: String? = null,
    @SerialName("material_id") val materialId: String,
    @SerialName("title") val title: String
)
