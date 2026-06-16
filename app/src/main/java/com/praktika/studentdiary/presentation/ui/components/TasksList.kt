package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.praktika.studentdiary.domain.model.Task
import com.praktika.studentdiary.presentation.events.ScheduleScreenEvents
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun TasksList(
    tasks: List<Task>,
    onEvent: (ScheduleScreenEvents) -> Unit,
) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет запланированных задач", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                val currentTime = LocalDateTime.now()
                val hoursUntilDue = ChronoUnit.HOURS.between(currentTime, task.dueDate)
                val daysUntilDue = ChronoUnit.DAYS.between(currentTime, task.dueDate)

                val cardColor = when {
                    task.status == "completed" -> MaterialTheme.colorScheme.surfaceBright
                    hoursUntilDue <= 24 -> Color(0xFFFFCDD2)
                    daysUntilDue <= 3 -> Color(0xFFFFF9C4)
                    else -> Color(0xFFC8E6C9)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = if (task.status == "completed") BorderStroke(
                        1.dp,
                        Color.Black
                    ) else null
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                modifier = Modifier.width(220.dp)
                            )
                            Text(
                                text = task.dueDate.format(DateTimeFormatter.ofPattern("MM.dd HH:mm:ss")),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                if (task.status != "completed") onEvent(
                                    ScheduleScreenEvents.CompleteTask(
                                        task.id
                                    )
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (task.status == "completed") {
                                    "Выполнено"
                                } else {
                                    "Готово"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

