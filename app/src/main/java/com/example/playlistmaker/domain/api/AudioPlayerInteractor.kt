package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface AudioPlayerInteractor {
  fun preparePlayer(url: String, onChangeState: (s: PlayerState) -> Unit)
  fun startPlayer()
  fun pausePlayer()
  fun stopPlayer()
  fun getPosition() : Long
  fun switchedStatePlayer(callback: (PlayerState) -> Unit)
}