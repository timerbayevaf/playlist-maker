package com.example.playlistmaker.playlist.domain.api

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.util.UUID

object PlaylistImageStorage {

    fun getImageDirectory(context: Context): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
    }

    fun createUniqueImageFile(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
        if (!dir.exists()) dir.mkdirs()
        val uniqueName = "image_${UUID.randomUUID()}.jpg"
        return File(dir, uniqueName)
    }

    fun moveTempToFinal(context: Context): String? {
        val temp = getTemporaryImageFile(context)
        if (!temp.exists()) return null

        val dst = createUniqueImageFile(context)
        return if (temp.renameTo(dst)) dst.toUri().toString() else null
    }

    fun getTemporaryImageFile(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "image_temp.jpg")
    }

    fun deleteImage(context: Context, imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) return
        val file = File(Uri.parse(imageUrl).path ?: return)
        if (file.exists()) file.delete()
    }
}