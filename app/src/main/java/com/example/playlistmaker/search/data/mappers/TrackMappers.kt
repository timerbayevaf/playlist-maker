package com.example.playlistmaker.search.data.mappers

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.TrackUI

fun Track.toUI() = TrackUI(
  trackId, trackName, artistName, trackTimeMillis,
  releaseDate, artworkUrl100, collectionName,
  primaryGenreName, country, previewUrl
)

fun TrackUI.toDomain() = Track(
  trackId, trackName, artistName, trackTimeMillis,
  releaseDate, artworkUrl100, collectionName,
  primaryGenreName, country, previewUrl
)
