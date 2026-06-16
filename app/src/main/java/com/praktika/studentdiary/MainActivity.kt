package com.praktika.studentdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.praktika.studentdiary.presentation.ui.App
import com.praktika.studentdiary.presentation.navigation.Navigator
import com.praktika.studentdiary.ui.theme.StudentDiaryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentDiaryTheme {
                App(navigator)
            }
        }
    }
}