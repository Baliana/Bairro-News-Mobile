package br.senai.sp.jandira.bairronews.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val fileName = context.contentResolver.getFileName(uri) ?: return null
    val tempFile = File(context.cacheDir, fileName)

    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun android.content.ContentResolver.getFileName(uri: Uri): String? =
    when (uri.scheme) {
        "content" -> getCursorFileName(uri)
        "file" -> uri.lastPathSegment
        else -> null
    }

private fun android.content.ContentResolver.getCursorFileName(uri: Uri): String? {
    val cursor = query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                return it.getString(nameIndex)
            }
        }
    }
    return null
}