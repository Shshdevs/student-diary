package com.praktika.studentdiary.presentation.model

import com.praktika.studentdiary.domain.model.Test
import com.praktika.studentdiary.domain.model.TestAttempt

data class SimulatorScreenUiModel(
    val materialId: String = "",
    val userId: String = "",
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val test: Test? = null,

    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Set<String>> = emptyMap(),

    val isTestFinished: Boolean = false,
    val currentScorePercent: Double? = null,
    val attemptHistory: List<TestAttempt> = emptyList(),

    val error: String? = null,
) {
    val totalQuestions: Int
        get() = test?.questions?.size ?: 0

    val isLastQuestion: Boolean
        get() = currentQuestionIndex >= totalQuestions - 1
}