package com.example.playlistmaker.medialibraries.utils

import com.example.playlistmaker.search.domain.models.Track

sealed class DetailedTracksState {
  object Default : DetailedTracksState()
  data class Content(val tracks: List<Track>) : DetailedTracksState()
  object Empty : DetailedTracksState()
  object Removed: DetailedTracksState()
}