package com.example.playlistmaker.audioplayer.domain.impl

import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerRepository
import com.example.playlistmaker.audioplayer.domain.models.PlayerState

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository):
  AudioPlayerInteractor {
  override fun preparePlayer(url: String, onChangeState: (s: PlayerState) -> Unit) {
    repository.prepare(url, onChangeState)
  }

  override fun startPlayer() {
    repository.start()
  }

  override fun pausePlayer() {
    repository.pause()
  }

  override fun stopPlayer() {
    repository.stop()
  }

  override fun getPosition(): Long = repository.getPosition()

  override fun switchedStatePlayer(callback: (PlayerState) -> Unit) {
    repository.switchedStatePlayer(callback)
  }
}