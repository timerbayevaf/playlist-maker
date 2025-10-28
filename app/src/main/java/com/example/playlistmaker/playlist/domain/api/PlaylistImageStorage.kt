package com.example.playlistmaker.playlist.domain.api

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import java.io.File

object PlaylistImageStorage {

    fun getImageDirectory(context: Context): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
    }

    fun moveTempToFinal(context: Context, playlistName: String): String? {
        val temp = getTemporaryImageFile(context)
        if (!temp.exists()) return null
        val dst = getImageFileForPlaylist(context, playlistName)
        if (dst.exists()) dst.delete()
        return if (temp.renameTo(dst)) dst.toUri().toString() else null
    }

    fun renameExistingImageFile(context: Context, oldName: String, newName: String): String? {
        val from = getImageFileForPlaylist(context, oldName)
        if (!from.exists()) return null
        val to = getImageFileForPlaylist(context, newName)
        if (to.exists()) to.delete()
        return if (from.renameTo(to)) to.toUri().toString() else null
    }


    fun getTemporaryImageFile(context: Context): File {
        val directory = getImageDirectory(context)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "image.jpg")
    }

    fun getImageFileForPlaylist(context: Context, playlistName: String): File {
        return File(getImageDirectory(context), "image_$playlistName.jpg")
    }
}