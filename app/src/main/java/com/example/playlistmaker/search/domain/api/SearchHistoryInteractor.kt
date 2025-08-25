package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
  fun searchTracks(expression: String, consumer: SearchConsumer)
  fun saveToHistory(track: Track)
  fun getHistory(): List<Track>
  fun clearHistory()

  interface SearchConsumer {
    fun consume(foundTracks: List<Track>?, errorMessage: String?)
  }
}
