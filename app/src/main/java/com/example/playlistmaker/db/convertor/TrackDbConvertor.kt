package com.example.playlistmaker.db.convertor

import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
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

    fun map(track: TrackEntity): Track {
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