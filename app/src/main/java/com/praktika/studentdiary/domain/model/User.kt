package com.praktika.studentdiary.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

data class User @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: LocalDate,
)