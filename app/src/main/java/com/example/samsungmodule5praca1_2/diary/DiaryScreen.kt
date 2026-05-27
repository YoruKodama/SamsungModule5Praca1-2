package com.example.samsungmodule5praca1_2.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    onNewEntry: () -> Unit,
    onEditEntry: (String) -> Unit,
    onBack: () -> Unit
) {
    var expandedFileName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой дневник") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Default.Add, contentDescription = "Новая запись")
            }
        }
    ) { padding ->
        if (viewModel.entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("У вас пока нет записей")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Нажмите +, чтобы создать первую",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(viewModel.entries, key = { it.fileName }) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .combinedClickable(
                                onClick = { onEditEntry(entry.fileName) },
                                onLongClick = { expandedFileName = entry.fileName }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (entry.title.isNotEmpty()) {
                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Text(
                                    text = entry.preview,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = entry.date,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Box {
                                IconButton(onClick = { expandedFileName = entry.fileName }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                                }
                                DropdownMenu(
                                    expanded = expandedFileName == entry.fileName,
                                    onDismissRequest = { expandedFileName = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Удалить") },
                                        onClick = {
                                            viewModel.deleteEntry(entry.fileName)
                                            expandedFileName = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
