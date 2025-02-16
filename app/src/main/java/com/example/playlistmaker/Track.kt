package com.example.playlistmaker

data class Track (
  //  val trackId: Int, // Уникальный идентификатор трека
  val trackName: String, // Название композиции
  val artistName: String, // Имя исполнителя
  val trackTime: String, // Продолжительность трека
  val artworkUrl100: String // Ссылка на изображение обложки
)