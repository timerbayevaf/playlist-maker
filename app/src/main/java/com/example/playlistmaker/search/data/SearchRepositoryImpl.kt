package com.example.playlistmaker.search.data

import com.bumptech.glide.load.engine.Resource
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchRepositoryImpl(private val networkClient: NetworkClient,private val storage: SearchHistoryStorage) : SearchHistoryRepository {
  override fun searchTracks(expression: String): List<Track> {
    val response = networkClient.doRequest(TracksSearchRequest(expression))
    if (response.resultCode == 200) {
      return (response as TracksSearchResponse).results.map {
        Track( it.trackId,
          it.trackName,
          it.artistName,
          it.trackTimeMillis,
          it.releaseDate,
          it.artworkUrl100,
          it.collectionName,
          it.primaryGenreName,
          it.country,
          it.previewUrl) }
    } else {
      return emptyList()
    }
  }

  override fun saveToHistory(track: Track) {
    storage.saveToHistory(track)
  }

  override fun getHistory(): List<Track> {
    return storage.getHistory()
  }

  override fun clearHistory() {
    storage.clearHistoryList()
  }
}