package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import java.util.concurrent.Executors

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {

  private val executor = Executors.newCachedThreadPool()

  override fun searchTracks(expression: String, consumer: SearchHistoryInteractor.SearchConsumer) {
    executor.execute {
      consumer.consume(repository.searchTracks(expression), null)
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