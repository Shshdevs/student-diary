package com.praktika.studentdiary.presentation.ui.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.praktika.studentdiary.R
import com.praktika.studentdiary.presentation.events.MaterialsScreenEvents
import com.praktika.studentdiary.presentation.model.MaterialsScreenUiModel
import com.praktika.studentdiary.presentation.ui.components.MaterialDetailView
import com.praktika.studentdiary.presentation.ui.components.MaterialsList
import com.praktika.studentdiary.presentation.viewmodel.MaterialsScreenViewModel

@Composable
fun MaterialsScreen(
    viewModel: MaterialsScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialsScreenContent(
        uiState = uiState, onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreenContent(
    uiState: MaterialsScreenUiModel,
    onEvent: (MaterialsScreenEvents) -> Unit,
) {
    val context = LocalContext.current
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onEvent(
                MaterialsScreenEvents.ImportPdf(
                    it, getFileName(context, uri) ?: "Импортиварованный материал"
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Материалы") }, actions = {
                IconButton(onClick = { pdfLauncher.launch("application/pdf") }) {
                    Icon(painterResource(R.drawable.add), contentDescription = "Добавить PDF")
                }
            })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.subjects) { subject ->
                    FilterChip(
                        selected = uiState.selectedSubjectId == subject.id,
                        onClick = { onEvent(MaterialsScreenEvents.SelectSubject(subject.id)) },
                        label = { Text(subject.name) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isGeneratingAi) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("ИИ обрабатывает конспект...", style = MaterialTheme.typography.bodySmall)
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.selectedMaterial != null) {
                MaterialDetailView(
                    material = uiState.selectedMaterial,
                    onBack = { onEvent(MaterialsScreenEvents.SelectMaterial(null)) },
                    onStartSimulator = { materialId ->
                        onEvent(
                            MaterialsScreenEvents.GoToSimulator(
                                materialId
                            )
                        )
                    })
            } else {
                MaterialsList(
                    materials = uiState.materials,
                    onMaterialClick = { onEvent(MaterialsScreenEvents.SelectMaterial(it)) },
                    onDeleteClick = { onEvent(MaterialsScreenEvents.DeleteMaterial(it.id)) })
            }
        }
    }
}


fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}
