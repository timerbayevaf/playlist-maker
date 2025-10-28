package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE track_id IN (:ids) ORDER BY timestamp DESC")
    suspend fun getTracksByIds(ids: List<Int>): List<TrackInPlaylistEntity>

    // Удалить трек по id
    @Query("DELETE FROM track_in_playlist_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    // Удалить все треки конкретного плейлиста (например, при удалении самого плейлиста)
    @Query("DELETE FROM track_in_playlist_table WHERE track_id IN (:ids)")
    suspend fun deleteTracksByIds(ids: List<Int>)

    @Query("SELECT EXISTS(SELECT 1 FROM track_in_playlist_table WHERE track_id = :trackId)")
    suspend fun isTrackExists(trackId: Int): Boolean
}