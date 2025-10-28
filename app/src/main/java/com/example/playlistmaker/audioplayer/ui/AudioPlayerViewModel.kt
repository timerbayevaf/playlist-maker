package com.example.playlistmaker.audioplayer.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.audioplayer.utils.AddToPlaylistStatus
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.medialibraries.utils.PlaylistState
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
  val audioPlayerInteractor: AudioPlayerInteractor,
  val favoriteInteractor: FavoriteTracksInteractor,
  private val playlistInteractor: PlaylistInteractor,

  ): ViewModel()  {
  companion object {
    private const val DELAY = 300L
    private const val DEFAULT_TIMER = 0L
    private const val TAG = "AudioPlayerViewModel"
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

  fun addTrackToPlaylist(track: Track, playlist: Playlist) {
    viewModelScope.launch {
      // 1. Проверяем, есть ли трек уже в плейлисте
      val trackExists = playlist.tracksIds.contains(track.trackId)

      if (trackExists) {
        Log.d(TAG, "Track already in playlist: ${playlist.name}")
        updateState { it.copy(addToPlaylistStatus = AddToPlaylistStatus.Exists(playlist)) }
        return@launch
      }

      // 2. Добавляем трек в таблицу track_in_playlist_table
      playlistInteractor.addDescriptionPlaylist(track)

      // 3. Добавляем ID трека в playlist_table
      playlistInteractor.addTrackInPlaylist(playlist.id, track.trackId.toString())

      Log.d(TAG, "Track added to playlist: ${playlist.name}")
      updateState { it.copy(addToPlaylistStatus = AddToPlaylistStatus.Added(playlist)) }
    }
  }

  fun resetAddToPlaylistStatus() {
    updateState { it.copy(addToPlaylistStatus = AddToPlaylistStatus.Default) }
  }

  fun loadPlaylists() {
    viewModelScope.launch {
      updateState { it.copy(playlistState = PlaylistState.Loading) }

      val playlists = playlistInteractor.getAllPlaylists().first()
      val newState =  if (playlists.isEmpty()) PlaylistState.Empty
                      else PlaylistState.Content(playlists)

      updateState { it.copy(playlistState = newState) }
    }
  }

  private fun updateState(transform: (AudioPlayerScreenState) -> AudioPlayerScreenState) {
    viewModelScope.launch {
      val current = screenStateLiveData.value ?: AudioPlayerScreenState()
      val newState = transform(current)
      Log.d(TAG, "updateState: $newState")
      screenStateLiveData.value = newState
    }
  }

  private fun updatePlayerState(newState: PlayerState) {
    updateState { current ->
      val isEndOfTrack = current.playerState == PlayerState.PLAYING && newState != PlayerState.PAUSED
      val currentTime = if (isEndOfTrack) DEFAULT_TIMER else current.currentTime
      current.copy(
        playerState = newState,
        currentTime = currentTime
      )
    }
  }

  fun preparePlayer(url: String, track: Track) {
    audioPlayerInteractor.preparePlayer(url) {newState ->
      when (newState) {
        PlayerState.PREPARED -> {
          timerJob?.cancel()
          updateFavoriteState(track)
          updatePlayerState(PlayerState.PREPARED)
        }
        PlayerState.DEFAULT -> {
          timerJob?.cancel()
          updatePlayerState(PlayerState.DEFAULT)
        }
        else -> Unit
      }
    }
  }

  fun onPause() {
    if (screenStateLiveData.value?.playerState == PlayerState.PLAYING) {
      timerJob?.cancel()
      audioPlayerInteractor.pausePlayer()
      updatePlayerState(PlayerState.PAUSED)
    }
  }

  fun updateFavoriteState(track: Track) {
    viewModelScope.launch {
      val isFavorite = favoriteInteractor.isTrackFavorite(track.trackId)
      updateState { it.copy(isFavorite = isFavorite) }
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
    updatePlayerState(PlayerState.PAUSED)
  }

  fun changePlayerState() {
    audioPlayerInteractor.switchedStatePlayer { state ->
      when (state) {
        PlayerState.PLAYING -> {
          startTimer()
          updatePlayerState(PlayerState.PLAYING)
        }
        PlayerState.PAUSED -> {
          timerJob?.cancel()
          updatePlayerState(PlayerState.PAUSED)
        }
        PlayerState.PREPARED -> {
          timerJob?.cancel()
          updatePlayerState(PlayerState.PREPARED)
        }
        else -> {
          timerJob?.cancel()
          updatePlayerState(PlayerState.DEFAULT)
        }
      }
    }
  }
}