package com.example.playlistmaker.tracks.domain.api

import com.example.playlistmaker.tracks.domain.models.Track

interface TracksRepository {
  fun searchTracks(expression: String): List<Track>
}