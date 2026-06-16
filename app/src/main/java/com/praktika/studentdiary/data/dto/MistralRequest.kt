package com.praktika.studentdiary.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MistralRequest(
    @SerialName("model")
    val model: String,

    @SerialName("messages")
    val messages: List<MistralMessage>,

    @SerialName("response_format")
    val responseFormat: MistralResponseFormat? = MistralResponseFormat(),
)
