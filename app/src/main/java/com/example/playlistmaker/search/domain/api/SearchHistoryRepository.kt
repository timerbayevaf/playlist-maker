package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
  fun searchTracks(expression: String): Flow<Resource<List<Track>>>
  fun saveToHistory(track: Track)
  fun getHistory(): List<Track>

  fun clearHistory()

}