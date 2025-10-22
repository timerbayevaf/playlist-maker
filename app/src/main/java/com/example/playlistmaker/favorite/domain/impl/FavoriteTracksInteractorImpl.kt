package com.example.playlistmaker.favorite.domain.impl

import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FavoriteTracksInteractorImpl(private val repository: FavoriteTracksRepository):
    FavoriteTracksInteractor {
    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }

    override suspend fun getIdsTracks(): Flow<List<Int>> {
        return repository.getIdsTracks()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        val ids = repository.getIdsTracks().first()
        return ids.contains(trackId)
    }
}