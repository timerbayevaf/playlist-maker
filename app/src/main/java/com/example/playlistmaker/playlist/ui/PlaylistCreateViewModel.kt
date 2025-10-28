package com.example.playlistmaker.playlist.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistCreateViewModel(private val context: Context,
                              private val interactor: PlaylistInteractor,
): ViewModel() {
    private val imageUrlLiveData = MutableLiveData<String>()
    fun observeImageUrl(): LiveData<String> = imageUrlLiveData

    suspend fun createNewPlaylist(
        name: String,
        description: String,
        imageUrl: String?,
        tracksIds: List<Int>,
    ) {
        var finalImageUrl = imageUrl

        // если пользователь выбрал картинку — переносим temp -> UUID
        val newFileUri = PlaylistImageStorage.moveTempToFinal(context)
        if (newFileUri != null) {
            finalImageUrl = newFileUri
        }

        val newPlaylist = Playlist(0, name, description, finalImageUrl, tracksIds)
        interactor.insertPlaylist(newPlaylist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        interactor.updatePlaylist(playlist)
    }
}