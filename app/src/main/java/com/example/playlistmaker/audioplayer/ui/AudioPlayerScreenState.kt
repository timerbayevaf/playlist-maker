package com.example.playlistmaker.audioplayer.ui

import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.audioplayer.utils.AddToPlaylistStatus
import com.example.playlistmaker.medialibraries.utils.PlaylistState

data class AudioPlayerScreenState(
  val playerState: PlayerState = PlayerState.DEFAULT,
  val currentTime: Long = 0L,
  val isFavorite: Boolean = false,
  val playlistState: PlaylistState = PlaylistState.Loading,
  val addToPlaylistStatus: AddToPlaylistStatus = AddToPlaylistStatus.Default
)
