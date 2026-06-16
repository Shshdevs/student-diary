package com.praktika.studentdiary.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.praktika.studentdiary.R
import com.praktika.studentdiary.domain.model.Material

@Composable
fun MaterialsList(
    materials: List<Material>,
    onMaterialClick: (Material) -> Unit,
    onDeleteClick: (Material) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(materials) { material ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMaterialClick(material) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(material.title, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onDeleteClick(material) }) {
                        Icon(painterResource(R.drawable.delete), contentDescription = "Удалить")
                    }
                }
            }
        }
    }
}