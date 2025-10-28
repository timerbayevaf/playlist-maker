package com.example.playlistmaker.medialibraries.ui.detailed

import com.example.playlistmaker.medialibraries.utils.DetailedTracksState
import com.example.playlistmaker.medialibraries.utils.ShareState
import com.example.playlistmaker.playlist.domain.models.Playlist

data class DetailedPlaylistScreenState(
    val playlist: Playlist? = null,
    val totalDuration: Int = 0,
    val shareState: ShareState = ShareState.Default,
    val tracksState: DetailedTracksState = DetailedTracksState.Default,
)