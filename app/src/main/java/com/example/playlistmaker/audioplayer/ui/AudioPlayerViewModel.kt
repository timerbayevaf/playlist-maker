package com.example.playlistmaker.audioplayer.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(val audioPlayerInteractor: AudioPlayerInteractor): ViewModel()  {
  companion object {
    private const val DELAY = 1000L
    private const val DEFAULT_TIMER = 0L
  }
  private val handler = Handler(Looper.getMainLooper())
  private val screenStateLiveData = MutableLiveData(AudioPlayerScreenState())
  fun getScreenStateLiveData(): LiveData<AudioPlayerScreenState> = screenStateLiveData
  private var timerJob: Job? = null

  private fun startTimer() {
    timerJob = viewModelScope.launch {
      while (isActive) {
        delay(DELAY)
        val position = audioPlayerInteractor.getPosition()
        screenStateLiveData.value = screenStateLiveData.value?.copy(currentTime = position)
      }
    }
  }


  private fun updateState(state: PlayerState) {
    val currentTime = screenStateLiveData.value?.currentTime ?: 0L
    screenStateLiveData.postValue(AudioPlayerScreenState(state, currentTime))
  }


  fun preparePlayer(url: String) {
    audioPlayerInteractor.preparePlayer(url) {newState ->
      when (newState) {
        PlayerState.PREPARED -> {
          updateState(PlayerState.PREPARED)
          timerJob?.cancel()
        }
        PlayerState.DEFAULT -> {
          updateState(PlayerState.DEFAULT)
          timerJob?.cancel()
        }
        else -> Unit
      }
    }
  }

  fun onStart() {
    startTimer()
    audioPlayerInteractor.startPlayer()
    updateState(PlayerState.PLAYING)
  }

  fun onPause() {
    if (screenStateLiveData.value?.playerState == PlayerState.PLAYING) {
      timerJob?.cancel()
      audioPlayerInteractor.pausePlayer()
      updateState(PlayerState.PAUSED)
    }
  }


  fun onResume() {
    timerJob?.cancel()
    updateState(PlayerState.PAUSED)
  }

  fun changePlayerState() {
    audioPlayerInteractor.switchedStatePlayer { state ->
      when (state) {
        PlayerState.PLAYING -> {
          startTimer()
          updateState(PlayerState.PLAYING)
        }
        PlayerState.PAUSED -> {
          timerJob?.cancel()
          updateState(PlayerState.PAUSED)
        }
        PlayerState.PREPARED -> {
          timerJob?.cancel()
          updateState(PlayerState.PREPARED)
        }
        else -> {
          timerJob?.cancel()
          updateState(PlayerState.DEFAULT)
        }
      }
    }
  }
}