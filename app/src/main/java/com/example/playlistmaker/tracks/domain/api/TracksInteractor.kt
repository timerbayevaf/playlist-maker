package com.example.playlistmaker.tracks.domain.api

import com.example.playlistmaker.tracks.domain.models.Track

interface TracksInteractor {
  fun searchTracks(expression: String, consumer: TracksConsumer)

  interface TracksConsumer {
    fun consume(foundTracks: List<Track>)
  }
}
