package com.example.playlistmaker.audioplayer.domain.api

import com.example.playlistmaker.audioplayer.domain.models.PlayerState

interface AudioPlayerRepository {
  fun prepare(url: String, onChangeState: (s: PlayerState) -> Unit)
  fun start()
  fun pause()
  fun stop()
  fun getPosition() : Long
  fun switchedStatePlayer(callback: (PlayerState) -> Unit)
}