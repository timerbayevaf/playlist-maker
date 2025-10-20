package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
  fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
  fun saveToHistory(track: Track)
  fun getHistory(): List<Track>
  fun clearHistory()

  interface SearchConsumer {
    fun consume(foundTracks: List<Track>?, errorMessage: String?)
  }
}
