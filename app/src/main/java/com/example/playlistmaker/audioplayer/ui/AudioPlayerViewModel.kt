package com.example.playlistmaker.audioplayer.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.creator.Creator

class AudioPlayerViewModel(val audioPlayerInteractor: AudioPlayerInteractor): ViewModel()  {
  companion object {
    private const val DELAY = 1000L
    fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
      initializer {
        AudioPlayerViewModel(Creator.provideAudioPlayerInteractor())
      }
    }

  }
  private val handler = Handler(Looper.getMainLooper())
  private val runnable = createUpdateTimerRunnable()
  private val statePlayerLiveData = MutableLiveData<PlayerState>()
  fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData

  private val currentTimeLiveData = MutableLiveData<Long>()
  fun getCurrentTimeLiveData(): LiveData<Long> = currentTimeLiveData

  private fun createUpdateTimerRunnable(): Runnable {
    return object : Runnable {
      override fun run() {
        val position = audioPlayerInteractor.getPosition()
        currentTimeLiveData.postValue(position)
        handler.postDelayed(this, AudioPlayerViewModel.DELAY)
      }
    }
  }

  init {
    audioPlayerInteractor.switchedStatePlayer { state ->
      statePlayerLiveData.postValue(state)
      if (state == PlayerState.DEFAULT) handler.removeCallbacks(runnable)
    }

  }

  fun preparePlayer(url: String) {
    audioPlayerInteractor.preparePlayer(url) {newState ->
      when (newState) {
        PlayerState.PREPARED, PlayerState.DEFAULT -> {
          statePlayerLiveData.postValue(PlayerState.PREPARED)
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
    statePlayerLiveData.postValue(PlayerState.PLAYING)
  }

  fun onPause() {
    handler.removeCallbacks(runnable)
    audioPlayerInteractor.pausePlayer()
    statePlayerLiveData.postValue(PlayerState.PAUSED)
  }

  fun onDestroy() {
    handler.removeCallbacks(runnable)
  }

  fun onResume() {
    handler.removeCallbacks(runnable)
    statePlayerLiveData.postValue(PlayerState.PAUSED)
  }

  fun changePlayerState() {
    audioPlayerInteractor.switchedStatePlayer { state ->
      when (state) {
        PlayerState.PLAYING -> {
          handler.removeCallbacks(runnable)
          handler.post(runnable)
          statePlayerLiveData.postValue(PlayerState.PLAYING)
        }
        PlayerState.PAUSED -> {
          handler.removeCallbacks(runnable)
          statePlayerLiveData.postValue(PlayerState.PAUSED)
        }
        PlayerState.PREPARED -> {
          handler.removeCallbacks(runnable)
          handler.post(runnable)
          statePlayerLiveData.postValue(PlayerState.PREPARED)
        }
        else -> {
          handler.removeCallbacks(runnable)
          statePlayerLiveData.postValue(PlayerState.DEFAULT)
        }
      }
    }
  }
}