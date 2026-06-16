package com.praktika.studentdiary.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MistralResponse(
    val choices: List<Choice>,
) {
    @Serializable
    data class Choice(val message: MistralMessage)
}