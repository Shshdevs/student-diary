package com.praktika.studentdiary.presentation.events

sealed class SimulatorScreenEvents {
    data class LoadData(val materialId: String, val userId: String) : SimulatorScreenEvents()

    data class GenerateTest(val materialTitle: String, val rawText: String) :
        SimulatorScreenEvents()

    data class ToggleAnswer(
        val questionId: String,
        val answerId: String,
        val isMultipleChoice: Boolean,
    ) : SimulatorScreenEvents()

    object NextQuestion : SimulatorScreenEvents()
    object FinishTest : SimulatorScreenEvents()
    object RetryTest : SimulatorScreenEvents()

    object DismissError : SimulatorScreenEvents()
}