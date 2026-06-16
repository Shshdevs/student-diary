package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MaterialDto(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    @SerialName("subject_id") val subjectId: String,
    val title: String,
    @SerialName("file_url") val fileUrl: String? = null,
    @SerialName("original_text") val originalText: String? = null,
    @SerialName("summary_text") val summaryText: String? = null,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
)

