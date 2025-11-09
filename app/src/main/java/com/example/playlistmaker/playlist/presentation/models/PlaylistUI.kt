package com.example.playlistmaker.playlist.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaylistUI(
    val id: Int, // уникальный идентификатор плейлиста
    val name: String, // Название плейлиста
    val description: String?, // Описание плейлиста
    val imageUrl: String?, // Ссылка на изображение плейлиста
    val tracksIds: List<Int> = emptyList(), // теперь всегда список, даже если пустой
): Parcelable {
    companion object {
        const val TRACK = "track"
    }
    val countTracks: Int get() = tracksIds.size // вычисляемое свойство
}
