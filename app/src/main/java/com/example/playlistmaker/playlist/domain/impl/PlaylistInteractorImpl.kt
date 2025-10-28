package com.example.playlistmaker.playlist.domain.impl

import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
       return repository.getAllPlaylists()
    }

    override suspend fun addTrackInPlaylist(playlistId: Int, trackId: String) {
        repository.addTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun addDescriptionPlaylist(track: Track) {
        repository.addDescriptionPlaylist(track)
    }

    override suspend fun deleteTrackById(trackId: Int) {
        repository.deleteTrackById(trackId)
    }

    override suspend fun getPlaylist(id: Int): Flow<Playlist?> {
        return repository.getPlaylist(id)
    }

    override suspend fun getTracksForPlaylist(playlistId: Int): Flow<List<Track>> {
        return repository.getTracksForPlaylist(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, track: Track) {
        repository.removeTrackFromPlaylist(playlistId, track)
    }
}