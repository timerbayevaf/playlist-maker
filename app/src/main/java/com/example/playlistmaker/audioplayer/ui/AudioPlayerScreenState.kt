package com.example.playlistmaker.audioplayer.ui

import com.example.playlistmaker.audioplayer.domain.models.PlayerState

data class AudioPlayerScreenState(
  val playerState: PlayerState = PlayerState.DEFAULT,
  val currentTime: Long = 0L
)
