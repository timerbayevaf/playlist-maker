package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface AudioPlayerRepository {
  fun prepare(url: String, onChangeState: (s: PlayerState) -> Unit)
  fun start()
  fun pause()
  fun stop()
  fun getPosition() : Long
  fun switchedStatePlayer(callback: (PlayerState) -> Unit)
}