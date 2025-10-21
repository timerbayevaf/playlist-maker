package com.example.playlistmaker.search.data

import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryStorage
import com.example.playlistmaker.search.domain.models.Track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(private val networkClient: NetworkClient, private val storage: SearchHistoryStorage) : SearchHistoryRepository {
  override fun searchTracks(expression: String): Flow<Result<List<Track>>> = flow {
    val response = networkClient.doRequest(TracksSearchRequest(expression))
    when (response.resultCode) {
      -1 -> {
        emit(Result.failure(Throwable(R.string.search_no_connection.toString())))
      }

      200 -> {
        with(response as TracksSearchResponse) {
          val data = response.results.map {
            Track(
              it.trackId,
              it.trackName,
              it.artistName,
              it.trackTimeMillis,
              it.releaseDate,
              it.artworkUrl100,
              it.collectionName,
              it.primaryGenreName,
              it.country,
              it.previewUrl
            )
          }
          emit(Result.success(data))
        }
      }
      else -> {
        emit(Result.failure(Throwable(R.string.search_internal_server_error.toString())))
      }
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