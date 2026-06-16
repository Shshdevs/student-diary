package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.praktika.studentdiary.presentation.events.SimulatorScreenEvents
import com.praktika.studentdiary.presentation.model.SimulatorScreenUiModel

@Composable
fun TestInProgressView(
    uiState: SimulatorScreenUiModel,
    onEvent: (SimulatorScreenEvents) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val test = uiState.test ?: return
    val currentQuestion = test.questions.getOrNull(uiState.currentQuestionIndex) ?: return

    val isMultipleChoice = currentQuestion.answers.count { it.isCorrect } > 1

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Вопрос ${uiState.currentQuestionIndex + 1} из ${uiState.totalQuestions}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentQuestion.answers) { answer ->
                val isSelected =
                    uiState.selectedAnswers[currentQuestion.id]?.contains(answer.id) == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onEvent(
                                SimulatorScreenEvents.ToggleAnswer(
                                    questionId = currentQuestion.id,
                                    answerId = answer.id,
                                    isMultipleChoice = isMultipleChoice
                                )
                            )
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isMultipleChoice) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = null
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = answer.answerText)
                    }
                }
            }
        }

        Button(
            onClick = {
                if (uiState.isLastQuestion) {
                    onEvent(SimulatorScreenEvents.FinishTest)
                } else {
                    onEvent(SimulatorScreenEvents.NextQuestion)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.selectedAnswers[currentQuestion.id]?.isNotEmpty() == true
        ) {
            Text(if (uiState.isLastQuestion) "Завершить тест" else "Следующий вопрос")
        }
    }
}