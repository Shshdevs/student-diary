package com.praktika.studentdiary.presentation.events

sealed class SimulatorScreenEvents {
    data class LoadData(val materialId: String) : SimulatorScreenEvents()
    object GenerateTest : SimulatorScreenEvents()
    data class ToggleAnswer(
        val questionId: String,
        val answerId: String,
        val isMultipleChoice: Boolean,
    ) : SimulatorScreenEvents()

    object NextQuestion : SimulatorScreenEvents()
    object FinishTest : SimulatorScreenEvents()
    object RetryTest : SimulatorScreenEvents()
    object DismissError : SimulatorScreenEvents()

    object OnGoBack : SimulatorScreenEvents()
}