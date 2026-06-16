package com.praktika.studentdiary.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MistralResponseFormat(
    val type: String = "json_object",
)