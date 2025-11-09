package com.example.playlistmaker.db.convertor

import com.example.playlistmaker.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackInPlaylistDbConvertor {
    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.releaseDate,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }

    fun map(track: TrackInPlaylistEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.releaseDate,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}