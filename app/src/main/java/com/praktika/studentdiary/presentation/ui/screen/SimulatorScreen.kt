package com.praktika.studentdiary.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.praktika.studentdiary.presentation.events.SimulatorScreenEvents
import com.praktika.studentdiary.presentation.model.SimulatorScreenUiModel
import com.praktika.studentdiary.presentation.ui.components.EmptyTestView
import com.praktika.studentdiary.presentation.ui.components.ErrorView
import com.praktika.studentdiary.presentation.ui.components.GeneratingView
import com.praktika.studentdiary.presentation.ui.components.TestInProgressView
import com.praktika.studentdiary.presentation.ui.components.TestResultView
import com.praktika.studentdiary.presentation.viewmodel.SimulatorViewModel

@Composable
fun SimulatorScreen(
    materialId: String,
    userId: String,
    materialTitle: String,
    rawText: String,
    viewModel: SimulatorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(materialId, userId) {
        viewModel.onEvent(SimulatorScreenEvents.LoadData(materialId, userId))
    }

    SimulatorScreenContent(
        uiState = uiState,
        materialTitle = materialTitle,
        rawText = rawText,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreenContent(
    uiState: SimulatorScreenUiModel,
    materialTitle: String,
    rawText: String,
    onEvent: (SimulatorScreenEvents) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.test?.title ?: "Тренажер") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    ErrorView(
                        error = uiState.error,
                        onDismiss = { onEvent(SimulatorScreenEvents.DismissError) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.isGenerating -> {
                    GeneratingView(modifier = Modifier.align(Alignment.Center))
                }

                uiState.test == null -> {
                    EmptyTestView(
                        onGenerateClick = {
                            onEvent(
                                SimulatorScreenEvents.GenerateTest(
                                    materialTitle,
                                    rawText
                                )
                            )
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.isTestFinished -> {
                    TestResultView(
                        uiState = uiState,
                        onRetry = { onEvent(SimulatorScreenEvents.RetryTest) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    TestInProgressView(
                        uiState = uiState,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

