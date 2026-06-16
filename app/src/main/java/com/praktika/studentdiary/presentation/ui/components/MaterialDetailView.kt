package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.praktika.studentdiary.R
import com.praktika.studentdiary.domain.model.Material

@Composable
fun MaterialDetailView(
    material: Material,
    onBack: () -> Unit,
    onStartSimulator: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Назад к списку")
        }

        Text(
            text = material.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Конспект:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = material.summary ?: "Конспект еще не сгенерирован.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (material.glossary.isNotEmpty()) {
            Text(
                text = "Глоссарий:",
                style = MaterialTheme.typography.titleMedium
            )
            Column(modifier = Modifier.padding(top = 8.dp)) {
                material.glossary.forEach { term ->
                    ListItem(
                        headlineContent = { Text(term.term) },
                        supportingContent = { Text(term.definition) }
                    )
                    HorizontalDivider()
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { onStartSimulator(material.id) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Icon(painterResource(R.drawable.goal), "Simulator")
                Spacer(Modifier.width(5.dp))
                Text("В Тренажер")
            }
        }
    }
}