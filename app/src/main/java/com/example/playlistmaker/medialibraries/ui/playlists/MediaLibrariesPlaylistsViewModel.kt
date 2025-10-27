package com.example.playlistmaker.medialibraries.ui.playlists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibraries.utils.PlaylistState
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MediaLibrariesPlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private val _stateLiveData = MutableLiveData<PlaylistState>()
    val stateLiveData: LiveData<PlaylistState> = _stateLiveData

    fun loadPlaylists() {
        renderState( PlaylistState.Loading)

        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists().first()
            Log.d("MediaLibrariesPlaylistsView", "loadPlaylists playlist start")

            val state = if (playlists.isEmpty()) {
                PlaylistState.Empty
            } else {
                PlaylistState.Content(playlists)
            }
            Log.d("MediaLibrariesPlaylistsView", "loadPlaylists playlist: ${state}")
            renderState( state)
        }
    }

    private fun renderState(state: PlaylistState) {
        _stateLiveData.postValue(state)
    }
}