package com.example.playlistmaker.playlist.domain.api

import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackInPlaylist(playlistId: Int, trackId: String)
    suspend fun deleteTrackById(trackId: Int)
    suspend fun addDescriptionPlaylist(track: Track)
    suspend fun getPlaylist(id: Int): Flow<Playlist?>
    suspend fun getTracksForPlaylist(playlistId: Int): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(playlistId: Int, track: Track)
}