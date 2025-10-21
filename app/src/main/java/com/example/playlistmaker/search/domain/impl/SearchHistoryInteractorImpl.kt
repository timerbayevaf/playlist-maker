package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {

  override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
    return repository.searchTracks(expression).map { result ->
      result.fold(
        onSuccess = { data -> Pair(data, null) },
        onFailure = { error -> Pair(null, error.message) })
      }
    }

  override fun getHistory(): List<Track> {
    return repository.getHistory()
  }

  override fun saveToHistory(track: Track) {
    repository.saveToHistory(track)
  }


  override fun clearHistory() {
    return repository.clearHistory()
  }
}