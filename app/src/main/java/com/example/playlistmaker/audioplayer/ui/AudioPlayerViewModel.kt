package com.example.playlistmaker.audioplayer.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState

class AudioPlayerViewModel(val audioPlayerInteractor: AudioPlayerInteractor): ViewModel()  {
  companion object {
    private const val DELAY = 1000L
  }
  private val handler = Handler(Looper.getMainLooper())
  private val runnable = createUpdateTimerRunnable()
  private val screenStateLiveData = MutableLiveData(AudioPlayerScreenState())
  fun getScreenStateLiveData(): LiveData<AudioPlayerScreenState> = screenStateLiveData

  private fun createUpdateTimerRunnable(): Runnable {
    return object : Runnable {
      override fun run() {
        val position = audioPlayerInteractor.getPosition()
        screenStateLiveData.value = screenStateLiveData.value?.copy(currentTime = position)
        handler.postDelayed(this, DELAY)
      }
    }
  }

  init {
    audioPlayerInteractor.switchedStatePlayer { state ->
      updateState(state)
      if (state == PlayerState.DEFAULT) handler.removeCallbacks(runnable)
    }
  }

  private fun updateState(state: PlayerState) {
    val currentTime = screenStateLiveData.value?.currentTime ?: 0L
    screenStateLiveData.postValue(AudioPlayerScreenState(state, currentTime))
  }


  fun preparePlayer(url: String) {
    audioPlayerInteractor.preparePlayer(url) {newState ->
      when (newState) {
        PlayerState.PREPARED, PlayerState.DEFAULT -> {
          updateState(PlayerState.PREPARED)
          handler.removeCallbacks(runnable)
        }
        else -> {
          handler.removeCallbacks(runnable)
        }
      }
    }
  }

  fun onStart() {
    audioPlayerInteractor.startPlayer()
    handler.post(runnable)
    updateState(PlayerState.PLAYING)
  }

  fun onPause() {
    handler.removeCallbacks(runnable)
    audioPlayerInteractor.pausePlayer()
    updateState(PlayerState.PAUSED)
  }

  fun onDestroy() {
    handler.removeCallbacks(runnable)
  }

  fun onResume() {
    handler.removeCallbacks(runnable)
    updateState(PlayerState.PAUSED)
  }

  fun changePlayerState() {
    audioPlayerInteractor.switchedStatePlayer { state ->
      when (state) {
        PlayerState.PLAYING -> {
          handler.removeCallbacks(runnable)
          handler.post(runnable)
          updateState(PlayerState.PLAYING)
        }
        PlayerState.PAUSED -> {
          handler.removeCallbacks(runnable)
          updateState(PlayerState.PAUSED)
        }
        PlayerState.PREPARED -> {
          handler.removeCallbacks(runnable)
          handler.post(runnable)
          updateState(PlayerState.PREPARED)
        }
        else -> {
          handler.removeCallbacks(runnable)
          updateState(PlayerState.DEFAULT)
        }
      }
    }
  }
}