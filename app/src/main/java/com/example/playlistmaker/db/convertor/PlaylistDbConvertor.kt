package com.example.playlistmaker.db.convertor

import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor {
    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imageUrl,
            convertTracksIdsToString(playlist.tracksIds),
            playlist.tracksIds.size
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val tracksIds = convertStringTracksIdsToList(entity.tracksIds)

        return Playlist(
            entity.id,
            entity.name,
            entity.description,
            entity.imageUrl,
            tracksIds
        )
    }

    fun addTrackToEntity(entity: PlaylistEntity, trackId: Int): PlaylistEntity {
        val currentList = convertStringTracksIdsToList(entity.tracksIds).toMutableList()
        if (!currentList.contains(trackId)) {
            currentList.add(trackId)
        }

        return entity.copy(
            tracksIds = convertTracksIdsToString(currentList),
            countTracks = currentList.size
        )
    }

    private fun convertTracksIdsToString(tracksIds: List<Int>): String {
        return gson.toJson(tracksIds)
    }

    private fun convertStringTracksIdsToList(tracksIds: String?): List<Int> {
        if (tracksIds.isNullOrBlank() || tracksIds == "null") return emptyList()

        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(tracksIds, type)
    }
}