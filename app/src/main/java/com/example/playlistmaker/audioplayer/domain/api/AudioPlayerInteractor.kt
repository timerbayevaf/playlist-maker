package com.example.playlistmaker.audioplayer.domain.api

import com.example.playlistmaker.audioplayer.domain.models.PlayerState

interface AudioPlayerInteractor {
  fun preparePlayer(url: String, onChangeState: (s: PlayerState) -> Unit)
  fun startPlayer()
  fun pausePlayer()
  fun getPosition() : Long
  fun switchedStatePlayer(callback: (PlayerState) -> Unit)
}