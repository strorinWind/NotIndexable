package ru.strorin.shareE.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object PathUtils {
    fun getPath(context: Context, uri: Uri): String {
        if (uri.scheme == "file") {
            if (uri.path != null) return uri.path!!
            return ""
        }
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, proj, null, null, null)

        cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return "file://" + cursor.getString(columnIndex)
        }
        return "file://"
    }
}