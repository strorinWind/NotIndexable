package ru.strorin.shareE.image

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images
import java.util.concurrent.TimeUnit


data class Image(val uri: Uri,
                 val name: String,
                 val date: Long = 0
)

object CameraHelper {

    fun getRandomCameraImage(context: Context): Image {
        return getImageListFromCameraFolderOlderThan(context, 6).random()
    }

    private fun getImageListFromCameraFolderOlderThan(context: Context, days: Long): List<Image> {
        val imageList = mutableListOf<Image>()

        val projection = arrayOf(
            Images.Media._ID,
            Images.Media.DISPLAY_NAME,
            Images.Media.MIME_TYPE,
            Images.Media.DATE_ADDED
        )

        val selection = "${Images.ImageColumns.DATE_ADDED} <= ?"

        val sortOrder = "${Images.Media.DISPLAY_NAME} ASC"
        val selectionArgs = arrayOf(
            (System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days)).toString()
        )

        val query = context.contentResolver.query(
            Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imageList += Image(contentUri, name, date)
            }
        }
        return imageList
    }
}