package com.praktika.studentdiary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktika.studentdiary.domain.repository.TestRepository
import com.praktika.studentdiary.presentation.events.SimulatorScreenEvents
import com.praktika.studentdiary.presentation.model.SimulatorScreenUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SimulatorViewModel @Inject constructor(
    private val testRepository: TestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorScreenUiModel())
    val uiState: StateFlow<SimulatorScreenUiModel> = _uiState.asStateFlow()

    fun onEvent(event: SimulatorScreenEvents) {
        when (event) {
            is SimulatorScreenEvents.LoadData -> loadData(event.materialId, event.userId)
            is SimulatorScreenEvents.GenerateTest -> generateTest(
                event.materialTitle,
                event.rawText
            )

            is SimulatorScreenEvents.ToggleAnswer -> toggleAnswer(
                event.questionId,
                event.answerId,
                event.isMultipleChoice
            )

            SimulatorScreenEvents.NextQuestion -> nextQuestion()
            SimulatorScreenEvents.FinishTest -> finishTest()
            SimulatorScreenEvents.RetryTest -> retryTest()
            SimulatorScreenEvents.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loadData(materialId: String, userId: String) {
        _uiState.update { it.copy(isLoading = true, materialId = materialId, userId = userId) }
        viewModelScope.launch {
            val testResult = testRepository.getTestForMaterial(materialId)

            testResult.onSuccess { test ->
                if (test != null) {
                    val historyResult = testRepository.getAttemptsHistory(test.id, userId)
                    val history = historyResult.getOrNull() ?: emptyList()

                    _uiState.update {
                        it.copy(isLoading = false, test = test, attemptHistory = history)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Ошибка загрузки теста"
                    )
                }
            }
        }
    }

    private fun generateTest(materialTitle: String, rawText: String) {
        val materialId = _uiState.value.materialId
        if (materialId.isBlank()) return

        _uiState.update { it.copy(isGenerating = true, error = null) }

        viewModelScope.launch {
            val result = testRepository.generateAndSaveTest(materialId, materialTitle, rawText)

            result.onSuccess { test ->
                _uiState.update { it.copy(isGenerating = false, test = test) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        error = error.message ?: "Не удалось сгенерировать тест"
                    )
                }
            }
        }
    }

    private fun toggleAnswer(questionId: String, answerId: String, isMultipleChoice: Boolean) {
        _uiState.update { state ->
            val currentSelected = state.selectedAnswers[questionId] ?: emptySet()

            val newSelected = if (isMultipleChoice) {
                if (currentSelected.contains(answerId)) {
                    currentSelected - answerId
                } else {
                    currentSelected + answerId
                }
            } else {
                // Если выбор одиночный, заменяем сет
                setOf(answerId)
            }

            state.copy(
                selectedAnswers = state.selectedAnswers.toMutableMap().apply {
                    put(questionId, newSelected)
                }
            )
        }
    }

    private fun nextQuestion() {
        val state = _uiState.value
        if (!state.isLastQuestion) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        }
    }

    private fun retryTest() {
        _uiState.update {
            it.copy(
                currentQuestionIndex = 0,
                selectedAnswers = emptyMap(),
                isTestFinished = false,
                currentScorePercent = null
            )
        }
    }

    private fun finishTest() {
        val state = _uiState.value
        val test = state.test ?: return

        var correctAnswersCount = 0

        test.questions.forEach { question ->
            val correctIds = question.answers.filter { it.isCorrect }.map { it.id }.toSet()
            val selectedIds = state.selectedAnswers[question.id] ?: emptySet()

            if (correctIds.isNotEmpty() && correctIds == selectedIds) {
                correctAnswersCount++
            }
        }

        val scorePercent = if (test.questions.isNotEmpty()) {
            (correctAnswersCount.toDouble() / test.questions.size) * 100
        } else 0.0

        _uiState.update {
            it.copy(isTestFinished = true, currentScorePercent = scorePercent)
        }

        viewModelScope.launch {
            val result = testRepository.saveTestAttempt(
                testId = test.id,
                userId = state.userId,
                scorePercent = scorePercent
            )

            result.onSuccess { newAttempt ->
                _uiState.update {
                    it.copy(attemptHistory = it.attemptHistory + newAttempt)
                }
            }
        }
    }
}