package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
  private val searchHistoryInteractor: SearchHistoryInteractor,
  application: Application
): ViewModel() {
  companion object {
    private const val SEARCH_DEBOUNCE_DELAY = 2000L
  }
  private val appContext = application.applicationContext
  private var lastState: StateSearch? = null
  private var latestSearchText: String? = null
  private var searchTrackStatusLiveData = MutableLiveData<StateSearch>()
  fun getSearchTrackStatusLiveData(): LiveData<StateSearch> = searchTrackStatusLiveData

  private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
    searchRequest(changedText)
  }

  init {
    showHistoryOrEmptyContent()
  }

  fun searchDebounce(changedText: String) {
    if (latestSearchText != changedText) {
      latestSearchText = changedText
      trackSearchDebounce(changedText)
    }
  }

  private fun searchRequest(newSearchText: String) {
    if (newSearchText.isNotEmpty()) {
      renderState(StateSearch.Loading)

      viewModelScope.launch {
        searchHistoryInteractor
          .searchTracks(newSearchText)
          .collect { pair ->
            processResult(pair.first, pair.second)
          }
      }

    } else showHistoryOrEmptyContent()
  }

  private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
    val trackList = mutableListOf<Track>()
    if (foundTracks != null) {
      trackList.addAll(foundTracks)
    }

    when {
      errorMessage != null -> {
        renderState(StateSearch.Error( appContext.getString(
          R.string.something_went_wrong)))
      }
      trackList.isEmpty() -> {
        renderState(StateSearch.Empty( appContext.getString(R.string.nothing_found)))
      }
      else -> {
        renderState(StateSearch.Content(tracks = trackList))
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
  }

  fun addTrackInHistoryList(track: Track) {
    searchHistoryInteractor.saveToHistory(track)
  }

  fun clearHistory() {
    searchHistoryInteractor.clearHistory()
    renderState(StateSearch.Content(emptyList()))
  }

  fun clearSearchText() {
    latestSearchText = ""
    showHistoryOrEmptyContent()
  }

  private fun showHistoryOrEmptyContent() {
    val tracksList = searchHistoryInteractor.getHistory()
    if (tracksList.isNotEmpty()) {
      renderState(StateSearch.History(tracksList))
    } else {
      renderState(StateSearch.Content(emptyList()))
    }
  }

  fun restoreState() {
    lastState?.let {
      searchTrackStatusLiveData.postValue(it)
    }
  }


  private fun renderState(state: StateSearch) {
    lastState = state
    searchTrackStatusLiveData.postValue(state)
  }
}