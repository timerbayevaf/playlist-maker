package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class StateSearch {
  object Loading : StateSearch()

  data class Content(
    val tracks: List<Track>
  ) : StateSearch()

  data class History(
    val tracks: List<Track>
  ) : StateSearch()

  data class Empty(
    val emptyMessage: String
  ) : StateSearch()

  data class Error(
    val errorMessage: String
  ) : StateSearch()

}