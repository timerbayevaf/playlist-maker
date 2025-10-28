package com.example.playlistmaker.playlist.mappers

import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.presentation.models.PlaylistUI

fun Playlist.toUI() = PlaylistUI(
    id,
    name,
    description,
    imageUrl,
    tracksIds
)

fun PlaylistUI.toDomain() = Playlist(
    id,
    name,
    description,
    imageUrl,
    tracksIds
)