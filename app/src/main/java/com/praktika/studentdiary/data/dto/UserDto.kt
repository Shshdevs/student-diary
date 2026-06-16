package com.praktika.studentdiary.data.dto

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class UserDto @OptIn(ExperimentalTime::class) constructor(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val createdAt: Instant
)