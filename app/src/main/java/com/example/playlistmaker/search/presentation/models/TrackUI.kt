package com.example.playlistmaker.search.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUI(
  val trackId: Int,
  val trackName: String,
  val artistName: String,
  val trackTimeMillis: Long,
  val releaseDate: String? = null,
  val artworkUrl100: String? = null,
  val collectionName: String? = null,
  val primaryGenreName: String? = null,
  val country: String? = null,
  val previewUrl: String? = null
) : Parcelable {
  companion object {
    const val TRACK = "track"
  }
}

