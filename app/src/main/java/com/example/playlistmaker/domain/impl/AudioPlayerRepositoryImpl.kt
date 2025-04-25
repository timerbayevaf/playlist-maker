package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.models.PlayerState

class AudioPlayerRepositoryImpl : AudioPlayerRepository {
  private var mediaPlayer = MediaPlayer()
  private var playerState = PlayerState.DEFAULT
  override fun prepare(url: String, onChangeState: (s: PlayerState) -> Unit) {

    mediaPlayer.apply {
      setDataSource(url)
      prepareAsync()
      setOnPreparedListener {
        playerState = PlayerState.PREPARED
        onChangeState(PlayerState.PREPARED)
      }
      setOnCompletionListener {
        playerState = PlayerState.PREPARED
        onChangeState(PlayerState.PREPARED)
      }
    }
  }

  override fun start() {
    mediaPlayer.start()
    playerState = PlayerState.PLAYING
  }

  override fun pause() {
    mediaPlayer.pause()
    playerState = PlayerState.PAUSED
  }

  override fun stop() {
    mediaPlayer.stop()
    mediaPlayer.release()
  }

  override fun getPosition(): Long = mediaPlayer.currentPosition.toLong()

  override fun switchedStatePlayer(callback: (PlayerState) -> Unit) {
    when (playerState) {
      PlayerState.PLAYING -> {
        pause()
        callback(PlayerState.PAUSED)
      }
      PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.DEFAULT -> {
        start()
        callback(PlayerState.PLAYING)
      }
    }
  }
}