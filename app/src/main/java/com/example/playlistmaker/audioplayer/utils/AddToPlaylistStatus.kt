package com.example.playlistmaker.audioplayer.utils

import com.example.playlistmaker.playlist.domain.models.Playlist

sealed class AddToPlaylistStatus {
    object Default : AddToPlaylistStatus()
    data class Exists(val playlist: Playlist) : AddToPlaylistStatus()
    data class Added(val playlist: Playlist) : AddToPlaylistStatus()
}

