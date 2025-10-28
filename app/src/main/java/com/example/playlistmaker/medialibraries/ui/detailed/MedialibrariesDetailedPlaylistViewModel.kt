package com.example.playlistmaker.medialibraries.ui.detailed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibraries.utils.DetailedTracksState
import com.example.playlistmaker.medialibraries.utils.ShareState
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MedialibrariesDetailedPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
):ViewModel() {
    companion object {
        private const val TAG = "MedialibrariesDetailedPlaylistViewModel"
    }
    private val _screenStateLiveData = MutableLiveData(DetailedPlaylistScreenState())
    val screenStateLiveData: LiveData<DetailedPlaylistScreenState> = _screenStateLiveData

    private fun updateState(transform: (DetailedPlaylistScreenState) -> DetailedPlaylistScreenState) {
        viewModelScope.launch {
            val current = screenStateLiveData.value ?: DetailedPlaylistScreenState()
            val newState = transform(current)
            _screenStateLiveData.value = newState
        }
    }

    fun loadPlaylistWithTracks(playlist: Playlist) {

        viewModelScope.launch {
            updateState { it.copy(
                tracksState = DetailedTracksState.Default,
                playlist = playlist
            ) }
            refreshPlaylistData(playlist.id)
        }
    }

    fun loadPlaylistById(id: Int) {
        viewModelScope.launch {
            updateState { it.copy(tracksState = DetailedTracksState.Default) }

            val playlist = playlistInteractor.getPlaylist(id).first()
            if (playlist == null) {
                Log.w(TAG, "Playlist with id=$id not found")
                updateState { it.copy(tracksState = DetailedTracksState.Empty) }
                return@launch
            }

            updateState { it.copy(playlist = playlist) }

            refreshPlaylistData(playlist.id)
        }
    }

    private fun formatPlaylistDuration(tracks: List<Track>): Int {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        val totalMinutes = (totalMillis / 60000).toInt()

        return totalMinutes
    }


    private suspend fun refreshPlaylistData(playlistId: Int) {
        val tracks = playlistInteractor.getTracksForPlaylist(playlistId).first()
        val totalMinutes = formatPlaylistDuration(tracks)

        val newTracksState = if (tracks.isEmpty()) {
            DetailedTracksState.Empty
        } else {
            DetailedTracksState.Content(tracks)
        }

        updateState {
            it.copy(
                tracksState = newTracksState,
                totalDuration = totalMinutes
            )
        }

    }

    fun sharePlaylist(playlist: Playlist, titlePrefix: String) {
        viewModelScope.launch {
            val tracks = playlistInteractor.getTracksForPlaylist(playlist.id).first()
            val message = buildShareMessage(playlist, tracks, titlePrefix)

            val newShareState:ShareState = if (tracks.isEmpty()) {
                ShareState.Empty
            } else {
                ShareState.Content(message)
            }

            updateState {
                it.copy(
                    shareState = newShareState,
                )
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)

            val allPlaylists = playlistInteractor.getAllPlaylists().first()

            val usedTrackIds = allPlaylists.flatMap { it.tracksIds }.toSet()

            playlist.tracksIds.forEach { trackId ->
                if (trackId !in usedTrackIds) {
                    playlistInteractor.deleteTrackById(trackId)
                }
            }

            updateState {
                it.copy(tracksState = DetailedTracksState.Removed)
            }
        }
    }

    private fun buildShareMessage(
        playlist: Playlist,
        tracks: List<Track>,
        titlePrefix: String
    ): String {
        return buildString {
            appendLine("$titlePrefix ${playlist.name}")
            playlist.description?.takeIf { it.isNotBlank() }?.let {
                appendLine(it)
            }

            appendLine("${playlist.countTracks} треков")
            appendLine()

            tracks.forEachIndexed { index, track ->
                val minutes = (track.trackTimeMillis / 1000 / 60).toInt()
                val seconds = (track.trackTimeMillis / 1000 % 60).toInt()
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${String.format("%d:%02d", minutes, seconds)})")
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, track: Track) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, track)

            val allPlaylists = playlistInteractor.getAllPlaylists().first()
            val trackStillUsed = allPlaylists.any { playlist ->
                playlist.tracksIds.contains(track.trackId)
            }

            if (!trackStillUsed) {
                playlistInteractor.deleteTrackById(track.trackId)
            }

            refreshPlaylistData(playlistId)
        }
    }
}