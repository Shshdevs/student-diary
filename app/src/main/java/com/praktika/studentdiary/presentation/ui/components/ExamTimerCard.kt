package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ExamTimerCard(nearestExamDate: LocalDateTime?) {
    var timeLeftText by remember(nearestExamDate) { mutableStateOf("Расчет времени...") }

    if (nearestExamDate != null) {
        LaunchedEffect(nearestExamDate) {
            while (true) {
                val now = LocalDateTime.now()
                if (nearestExamDate.isBefore(now)) {
                    timeLeftText = "Сессия / экзамен уже начались!"
                } else {
                    val duration = Duration.between(now, nearestExamDate)
                    val days = duration.toDays()
                    val hours = duration.toHours() % 24
                    val minutes = duration.toMinutes() % 60
                    timeLeftText = "$days дн. $hours ч. $minutes мин."
                }
                delay(60000.milliseconds)
            }
        }
    } else {
        timeLeftText = "Нет предстоящих экзаменов"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("До ближайшего экзамена:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timeLeftText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}