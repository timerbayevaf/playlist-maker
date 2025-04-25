package com.example.playlistmaker.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackDto (
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
): Parcelable