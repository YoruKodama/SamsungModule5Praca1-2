package com.example.samsungmodule5praca1_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.samsungmodule5praca1_2.diary.DiaryEditScreen
import com.example.samsungmodule5praca1_2.diary.DiaryScreen
import com.example.samsungmodule5praca1_2.diary.DiaryViewModel
import com.example.samsungmodule5praca1_2.gallery.GalleryScreen
import com.example.samsungmodule5praca1_2.gallery.GalleryViewModel
import com.example.samsungmodule5praca1_2.ui.theme.SamsungModule5Praca1_2Theme

sealed class Screen {
    object Home : Screen()
    object Diary : Screen()
    object Gallery : Screen()
    data class DiaryEdit(val fileName: String?) : Screen()
}

class MainActivity : ComponentActivity() {

    private val diaryViewModel: DiaryViewModel by viewModels()
    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SamsungModule5Praca1_2Theme {
                var screen by remember { mutableStateOf<Screen>(Screen.Home) }

                when (val s = screen) {
                    is Screen.Home -> HomeScreen(
                        onDiary = { screen = Screen.Diary },
                        onGallery = { screen = Screen.Gallery }
                    )
                    is Screen.Diary -> DiaryScreen(
                        viewModel = diaryViewModel,
                        onNewEntry = { screen = Screen.DiaryEdit(null) },
                        onEditEntry = { fileName -> screen = Screen.DiaryEdit(fileName) },
                        onBack = { screen = Screen.Home }
                    )
                    is Screen.DiaryEdit -> DiaryEditScreen(
                        viewModel = diaryViewModel,
                        fileName = s.fileName,
                        onBack = { screen = Screen.Diary },
                        onSaved = { screen = Screen.Diary }
                    )
                    is Screen.Gallery -> GalleryScreen(
                        viewModel = galleryViewModel,
                        onBack = { screen = Screen.Home }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onDiary: () -> Unit, onGallery: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Модуль 5", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Выберите задание", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = onDiary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Задание 1: Дневник")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onGallery,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Задание 2: Галерея фото")
            }
        }
    }
}
