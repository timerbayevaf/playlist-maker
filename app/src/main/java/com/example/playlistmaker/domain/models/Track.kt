package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
  val trackId: Int, // Уникальный идентификатор трека
  val trackName: String, // Название композиции
  val artistName: String, // Имя исполнителя
  val trackTimeMillis: Long, // Продолжительность трека
  val releaseDate: String?, // Год релиза трека
  val artworkUrl100: String?, // Ссылка на изображение обложки
  val collectionName: String?, // Название альбома
  val primaryGenreName: String?, // Жанр трека
  val country: String?, // Страна исполнителя
  val previewUrl: String? // Ссылка на отрывок трека
): Parcelable {
  companion object {
    const val TRACK = "track"
  }
  // Функция для получения URL обложки высокого качества
  fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")

  fun getFormattedReleaseYear(): String {
    return this.releaseDate?.take(4) ?: ""
  }
}