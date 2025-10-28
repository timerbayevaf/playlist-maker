package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.dao.PlaylistDao
import com.example.playlistmaker.db.dao.TrackDao
import com.example.playlistmaker.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}

