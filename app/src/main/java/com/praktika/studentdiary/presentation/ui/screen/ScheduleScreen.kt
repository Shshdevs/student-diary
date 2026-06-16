package com.praktika.studentdiary.presentation.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.praktika.studentdiary.R
import com.praktika.studentdiary.presentation.events.ScheduleScreenEvents
import com.praktika.studentdiary.presentation.model.ScheduleUiModel
import com.praktika.studentdiary.presentation.ui.components.SubjectsList
import com.praktika.studentdiary.presentation.ui.components.TasksList
import com.praktika.studentdiary.presentation.ui.dialogs.AddSubjectDialog
import com.praktika.studentdiary.presentation.ui.dialogs.AddTaskDialog
import com.praktika.studentdiary.presentation.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScheduleScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreenContent(
    uiState: ScheduleUiModel,
    onEvent: (ScheduleScreenEvents) -> Unit,
) {
    val tabs = listOf("Задачи", "Предметы")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage == 0) {
                        onEvent(ScheduleScreenEvents.OnShowAddTaskDialog(true))
                    } else {
                        onEvent(ScheduleScreenEvents.OnShowAddSubjectDialog(true))
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(painterResource(R.drawable.add), contentDescription = "Добавить")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> TasksList(tasks = uiState.tasks, onEvent = onEvent)
                    1 -> SubjectsList(subjects = uiState.subjects, onEvent = onEvent)
                }
            }
        }

        if (uiState.showAddTaskDialog) {
            AddTaskDialog(
                subjects = uiState.subjects,
                onDismiss = { onEvent(ScheduleScreenEvents.OnShowAddTaskDialog(false)) },
                onConfirm = { subjectId, title, type, date ->
                    onEvent(ScheduleScreenEvents.CreateTask(subjectId, title, type, date))
                }
            )
        }

        if (uiState.showAddSubjectDialog) {
            AddSubjectDialog(
                onDismiss = { onEvent(ScheduleScreenEvents.OnShowAddSubjectDialog(false)) },
                onConfirm = { name ->
                    onEvent(ScheduleScreenEvents.CreateSubject(name))
                }
            )
        }
    }

}