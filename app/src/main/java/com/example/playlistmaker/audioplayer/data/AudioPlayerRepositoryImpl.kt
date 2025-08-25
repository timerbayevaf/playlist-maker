package com.example.playlistmaker.audioplayer.data

import android.media.MediaPlayer
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerRepository
import com.example.playlistmaker.audioplayer.domain.models.PlayerState

class AudioPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : AudioPlayerRepository {
  private var playerState = PlayerState.DEFAULT
  private var onChangeState: ((PlayerState) -> Unit)? = null

  override fun prepare(url: String, onChangeState: (s: PlayerState) -> Unit) {
    this.onChangeState = onChangeState
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
        mediaPlayer.seekTo(0)
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