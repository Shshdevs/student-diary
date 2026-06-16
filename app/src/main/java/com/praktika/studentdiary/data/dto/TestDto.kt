package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TestDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("material_id") val materialId: String,
    @SerialName("title") val title: String,
)
