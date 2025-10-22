package com.example.playlistmaker.medialibraries.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.medialibraries.utils.FavoriteTrackState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaLibrariesFavoriteTracksViewModel(
    private val interactor: FavoriteTracksInteractor
): ViewModel() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
    private val stateLiveData = MutableLiveData<FavoriteTrackState>()
    fun observeState(): LiveData<FavoriteTrackState> = stateLiveData
    private var isClickAllowed = true

    fun fillData() {
        renderState(FavoriteTrackState.Loading)
        viewModelScope.launch {
            interactor
                .getTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun renderState(state: FavoriteTrackState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        val actualState = if (tracks.isEmpty()) {
            FavoriteTrackState.Empty
        } else {
            FavoriteTrackState.Content(tracks)
        }
        renderState(actualState)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }
}