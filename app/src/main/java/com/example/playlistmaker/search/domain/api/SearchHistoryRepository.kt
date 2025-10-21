package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
  fun searchTracks(expression: String): Flow<Result<List<Track>>>
  fun saveToHistory(track: Track)
  fun getHistory(): List<Track>

  fun clearHistory()

}