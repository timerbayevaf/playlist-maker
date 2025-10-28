package com.example.playlistmaker.playlist.data

import android.util.Log
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.db.convertor.TrackDbConvertor
import com.example.playlistmaker.db.convertor.TrackInPlaylistDbConvertor
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertorPlaylist: PlaylistDbConvertor,
    private val convertorTracksInPlaylist: TrackInPlaylistDbConvertor
): PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) = withContext(Dispatchers.IO)  {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO)  {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }.flowOn(Dispatchers.IO)

    override suspend fun addTrackInPlaylist(playlistId: Int, trackId: String) = withContext(Dispatchers.IO) {
        val dao = appDatabase.playlistDao()
        val entity = dao.getPlaylistById(playlistId) ?: return@withContext

        val trackIdInt = trackId.toIntOrNull() ?:  return@withContext
        val updated = convertorPlaylist.addTrackToEntity(entity, trackIdInt)

        dao.updatePlaylist(updated)
    }

    override suspend fun deleteTrackById(trackId: Int) = withContext(Dispatchers.IO) {
        appDatabase.trackInPlaylistDao().deleteTrackById(trackId)
    }

    override suspend fun addDescriptionPlaylist(track: Track) = withContext(Dispatchers.IO)  {
        val trackDao = appDatabase.trackInPlaylistDao()
        val exists = trackDao.isTrackExists(track.trackId)
        if (!exists) {
            val trackEntity = convertFromTrackToTrackEntity(track)
            appDatabase.trackInPlaylistDao().insertTrackInPlaylist(trackEntity)
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, track: Track) = withContext(Dispatchers.IO) {
        val dao = appDatabase.playlistDao()
        val entity = dao.getPlaylistById(playlistId) ?: return@withContext

        val playlist = convertorPlaylist.map(entity)
        val updatedTrackIds = playlist.tracksIds.toMutableList().apply {
            remove(track.trackId)
        }

        val updatedEntity = convertorPlaylist.map(
            playlist.copy(tracksIds = updatedTrackIds)
        )

        dao.updatePlaylist(updatedEntity)
    }

    override fun getPlaylist(id: Int): Flow<Playlist?> = flow {
        val playlistEntity = appDatabase.playlistDao().getById(id)
        emit(playlistEntity?.let { convertFromEntityToPlaylist(it) })
    }.flowOn(Dispatchers.IO)

    override fun getTracksForPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val playlistEntity = appDatabase.playlistDao().getById(playlistId)

        if (playlistEntity == null) {
            emit(emptyList())
            return@flow
        }

        val trackIdsList = convertorPlaylist.map(playlistEntity).tracksIds

        if (trackIdsList.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val trackEntities = appDatabase.trackInPlaylistDao().getTracksByIds(trackIdsList)
        val trackMap = trackEntities.associateBy { it.trackId }

        emit(trackIdsList.mapNotNull { id -> trackMap[id]?.let { convertorTracksInPlaylist.map(it) } })
    }.flowOn(Dispatchers.IO)

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> convertorPlaylist.map(playlist) }
    }

    private fun convertFromEntityToPlaylist(playlist: PlaylistEntity): Playlist  {
        return convertorPlaylist.map(playlist)
    }

    private fun convertFromPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return convertorPlaylist.map(playlist)
    }

    private fun convertFromTrackToTrackEntity(track: Track): TrackInPlaylistEntity {
        return convertorTracksInPlaylist.map(track)
    }
}