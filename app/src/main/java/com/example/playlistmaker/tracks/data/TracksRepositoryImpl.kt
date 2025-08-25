package com.example.playlistmaker.tracks.data

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.tracks.domain.api.TracksRepository
import com.example.playlistmaker.tracks.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
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
}