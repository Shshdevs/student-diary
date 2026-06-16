package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummaryStatisticsCard(
    averageScore: Float,
    completedTests: Int,
    timeDistribution: Map<String, Int>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Сводная статистика", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Средний балл", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = String.format("%.1f%%", averageScore),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column {
                    Text("Пройдено тестов", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = completedTests.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Распределение времени (минуты)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (timeDistribution.isEmpty()) {
                Text(
                    "Нет записей затраченного времени",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                val maxMinutes = timeDistribution.values.maxOrNull() ?: 1

                timeDistribution.forEach { (subject, minutes) ->
                    val barProgress = minutes.toFloat() / maxMinutes.toFloat()

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(subject, style = MaterialTheme.typography.bodyMedium)
                            Text("$minutes мин.", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { barProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}