package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
  private val searchHistoryInteractor: SearchHistoryInteractor,
  application: Application
): ViewModel() {
  companion object {
    private val SEARCH_REQUEST_TOKEN = Any()
    private const val SEARCH_DEBOUNCE_DELAY = 2000L
  }
  private val appContext = application.applicationContext
  private var lastState: StateSearch? = null
  private var latestSearchText: String? = null
  private val handler = Handler(Looper.getMainLooper())
  private var searchTrackStatusLiveData = MutableLiveData<StateSearch>()
  fun getSearchTrackStatusLiveData(): LiveData<StateSearch> = searchTrackStatusLiveData


  init {
    showHistoryOrEmptyContent()
  }

  fun searchDebounce(changedText: String) {
    if (latestSearchText == changedText) {
      return
    }
    this.latestSearchText = changedText
    handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

    val searchRunnable = Runnable { searchRequest(changedText) }

    val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
    handler.postAtTime(
      searchRunnable,
      SEARCH_REQUEST_TOKEN,
      postTime,
    )
  }

  private fun searchRequest(newSearchText: String) {
    if (newSearchText.isNotEmpty()) {
      renderState(StateSearch.Loading)
      searchHistoryInteractor.searchTracks(newSearchText, object : SearchHistoryInteractor.SearchConsumer {
        override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
          handler.post {
            val trackList = mutableListOf<Track>()
            if (foundTracks != null) {
              trackList.addAll(foundTracks)
            }

            when {
              errorMessage != null -> {
                renderState(StateSearch.Error(appContext.getString(R.string.something_went_wrong)))
              }

              trackList.isEmpty() -> {
                renderState(StateSearch.Empty(appContext.getString(R.string.nothing_found)))
              }
              else -> {
                renderState(StateSearch.Content(tracks = trackList))
              }
            }
          }
        }
      }
      )
    } else showHistoryOrEmptyContent()
  }

  override fun onCleared() {
    super.onCleared()
    handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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