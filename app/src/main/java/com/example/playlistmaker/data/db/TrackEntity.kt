package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "track_id")
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