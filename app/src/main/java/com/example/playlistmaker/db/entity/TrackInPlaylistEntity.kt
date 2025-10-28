package com.example.playlistmaker.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_in_playlist_table")
data class TrackInPlaylistEntity (
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "track_id")
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val releaseDate: String? = null,
    val artworkUrl100: String? = null,
    val collectionName: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null,
    val timestamp: Long // Время добавления в избранное для сортировки
)