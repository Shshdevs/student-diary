package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.praktika.studentdiary.domain.model.Task
import com.praktika.studentdiary.presentation.events.ScheduleScreenEvents
import java.time.format.DateTimeFormatter

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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(task.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                task.dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(onClick = { onEvent(ScheduleScreenEvents.CompleteTask(task.id)) }) {
                            Text("Готово")
                        }
                    }
                }
            }
        }
    }
}

