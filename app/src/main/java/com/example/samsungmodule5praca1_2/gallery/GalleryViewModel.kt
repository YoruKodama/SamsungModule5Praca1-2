package com.example.samsungmodule5praca1_2.gallery

import android.app.Application
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    val photos = mutableStateListOf<File>()

    init {
        loadPhotos()
    }

    // сканирование при запуске и при добавлении нового фото
    fun loadPhotos() {
        val dir = getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        photos.clear()
        dir?.listFiles { f -> f.name.endsWith(".jpg") || f.name.endsWith(".jpeg") }
            ?.sortedByDescending { it.lastModified() }
            ?.let { photos.addAll(it) }
    }

    fun addPhoto(file: File) {
        photos.add(0, file)
    }

    fun createPhotoFile(): File {
        val dir = getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val name = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        return File(dir, name)
    }

    // экспорт фото в общую галерею через MediaStore (Android 10+)
    fun exportToGallery(file: File): Boolean {
        return try {
            val context = getApplication<Application>()
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyGallery")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return false

            context.contentResolver.openOutputStream(uri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }
}
