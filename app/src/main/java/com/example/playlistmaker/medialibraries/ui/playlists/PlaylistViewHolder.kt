package com.example.playlistmaker.medialibraries.ui.playlists

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) = with(binding) {
        playlistName.text = playlist.name
        countTracks.text = getTrackCountText(playlist.countTracks)
        loadPlaylistImage(playlist)
    }

    private fun getTrackCountText(count: Int): String {
        return  if (count == 0) {
            itemView.context.getString(R.string.no_tracks) // <-- используем отдельную строку
        } else {
            itemView.resources.getQuantityString(
                R.plurals.playlist_count_tracks,
                count,
                count
            )
        }
    }

    private fun loadPlaylistImage(playlist: Playlist) {
        val imageFile = File(
            itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist/image_${playlist.name}.jpg"
        )

        val source = when {
            !playlist.imageUrl.isNullOrEmpty() -> playlist.imageUrl
            imageFile.exists() -> imageFile
            else -> null
        }

        Glide.with(itemView)
            .load(source)
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(8))
            .into(binding.playlistImage)
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistViewBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }
}
