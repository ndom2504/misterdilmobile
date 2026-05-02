package com.example.misterdil.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileName(context: Context, uri: Uri): String {
    var name = "fichier"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && idx != -1) name = cursor.getString(idx)
    }
    return name
}

const val FILE_MSG_PREFIX = "__FILE__:"
