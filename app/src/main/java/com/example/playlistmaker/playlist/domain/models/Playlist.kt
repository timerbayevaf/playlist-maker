package com.example.playlistmaker.playlist.domain.models

data class Playlist (
    val id: Int, // уникальный идентификатор плейлиста
    val name: String, // Название плейлиста
    val description: String?, // Описание плейлиста
    val imageUrl: String?, // Ссылка на изображение плейлиста
    val tracksIds: List<Int> = emptyList(), // теперь всегда список, даже если пустой
) {
    val countTracks: Int get() = tracksIds.size // вычисляемое свойство
}