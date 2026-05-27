package com.example.samsungmodule5praca1_2.diary

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    val entries = mutableStateListOf<DiaryEntry>()

    init {
        loadEntries()
    }

    // вызывается один раз при запуске
    private fun loadEntries() {
        val filesDir = getApplication<Application>().filesDir
        val files = filesDir.listFiles { f -> f.name.endsWith(".txt") }
            ?.sortedByDescending { it.lastModified() }
            ?: return

        for (file in files) {
            entries.add(fileToEntry(file))
        }
    }

    private fun fileToEntry(file: File): DiaryEntry {
        val nameWithoutExt = file.nameWithoutExtension
        val underscoreIdx = nameWithoutExt.indexOf('_')
        val timestamp = if (underscoreIdx > 0) {
            nameWithoutExt.substring(0, underscoreIdx).toLongOrNull() ?: file.lastModified()
        } else {
            nameWithoutExt.toLongOrNull() ?: file.lastModified()
        }
        val title = if (underscoreIdx > 0) nameWithoutExt.substring(underscoreIdx + 1) else ""
        val content = file.readText()
        val preview = content.take(40)
        val dateStr = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
        return DiaryEntry(file.name, title, preview, dateStr)
    }

    fun getTitle(fileName: String): String {
        val nameWithoutExt = fileName.removeSuffix(".txt")
        val idx = nameWithoutExt.indexOf('_')
        return if (idx > 0) nameWithoutExt.substring(idx + 1) else ""
    }

    fun readContent(fileName: String): String {
        val file = File(getApplication<Application>().filesDir, fileName)
        return if (file.exists()) file.readText() else ""
    }

    // добавляет в начало списка без пересканирования папки
    fun saveEntry(fileName: String?, title: String, content: String) {
        val context = getApplication<Application>()

        if (fileName != null) {
            // редактирование существующей записи
            val file = File(context.filesDir, fileName)
            val timestamp = file.nameWithoutExtension.substringBefore("_")
            val newName = if (title.isNotEmpty()) "${timestamp}_${title}.txt" else "${timestamp}.txt"

            if (newName != fileName) {
                file.delete()
                val newFile = File(context.filesDir, newName)
                newFile.writeText(content)
                val idx = entries.indexOfFirst { it.fileName == fileName }
                if (idx >= 0) entries[idx] = fileToEntry(newFile)
            } else {
                file.writeText(content)
                val idx = entries.indexOfFirst { it.fileName == fileName }
                if (idx >= 0) entries[idx] = fileToEntry(file)
            }
        } else {
            // новая запись — добавляем в начало без пересканирования
            val timestamp = System.currentTimeMillis()
            val newName = if (title.isNotEmpty()) "${timestamp}_${title}.txt" else "${timestamp}.txt"
            val file = File(context.filesDir, newName)
            file.writeText(content)
            entries.add(0, fileToEntry(file))
        }
    }

    // удаляет из списка по имени файла без пересканирования
    fun deleteEntry(fileName: String) {
        val file = File(getApplication<Application>().filesDir, fileName)
        file.delete()
        entries.removeAll { it.fileName == fileName }
    }
}
