package com.example.playlistmaker.audioplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
  val audioPlayerInteractor: AudioPlayerInteractor,
  val favoriteInteractor: FavoriteTracksInteractor
): ViewModel()  {
  companion object {
    private const val DELAY = 300L
    private const val DEFAULT_TIMER = 0L
  }
  private val screenStateLiveData = MutableLiveData(AudioPlayerScreenState())
  fun getScreenStateLiveData(): LiveData<AudioPlayerScreenState> = screenStateLiveData
  private var timerJob: Job? = null

  private fun startTimer() {
    timerJob?.cancel()

    timerJob = viewModelScope.launch {
      while (isActive) {
        delay(DELAY)
        val position = audioPlayerInteractor.getPosition()
        screenStateLiveData.value = screenStateLiveData.value?.copy(currentTime = position)
      }
    }
  }

  private fun updateState(state: PlayerState) {
    val previousState = screenStateLiveData.value
    val isEndOfTrack = screenStateLiveData.value?.playerState == PlayerState.PLAYING && state !== PlayerState.PAUSED
    val currentTime = if (isEndOfTrack) 0L else screenStateLiveData.value?.currentTime ?: DEFAULT_TIMER
    screenStateLiveData.postValue( previousState?.copy(
      playerState = state,
      currentTime = currentTime
    ) ?: AudioPlayerScreenState(state, currentTime))
  }

  fun preparePlayer(url: String, track: Track) {
    audioPlayerInteractor.preparePlayer(url) {newState ->
      when (newState) {
        PlayerState.PREPARED -> {
          updateState(PlayerState.PREPARED)
          timerJob?.cancel()
          updateFavoriteState(track)
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

  fun updateFavoriteState(track: Track) {
    viewModelScope.launch {
      val isFavorite = favoriteInteractor.isTrackFavorite(track.trackId)
      screenStateLiveData.postValue(screenStateLiveData.value?.copy(isFavorite = isFavorite))
    }
  }

  fun onFavoriteClicked(track: Track) {
    viewModelScope.launch {
      val isFav = screenStateLiveData.value?.isFavorite == true
      if (isFav) {
        favoriteInteractor.deleteTrack(track)
      } else {
        favoriteInteractor.insertTrack(track)
      }
      updateFavoriteState(track)
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