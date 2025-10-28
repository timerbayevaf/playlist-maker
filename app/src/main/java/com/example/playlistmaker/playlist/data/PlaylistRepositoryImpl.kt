package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
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
import kotlinx.coroutines.flow.map
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

    override suspend fun addDescriptionPlaylist(track: Track) = withContext(Dispatchers.IO)  {
        val trackEntity = convertFromTrackToTrackEntity(track)
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(trackEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> convertorPlaylist.map(playlist) }
    }

    private fun convertFromPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return convertorPlaylist.map(playlist)
    }

    private fun convertFromTrackToTrackEntity(track: Track): TrackInPlaylistEntity {
        return convertorTracksInPlaylist.map(track)
    }
}