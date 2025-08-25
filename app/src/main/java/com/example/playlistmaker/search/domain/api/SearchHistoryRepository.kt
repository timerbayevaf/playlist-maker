package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
  fun searchTracks(expression: String): List<Track>
  fun saveToHistory(track: Track)
  fun getHistory(): List<Track>

  fun clearHistory()

}