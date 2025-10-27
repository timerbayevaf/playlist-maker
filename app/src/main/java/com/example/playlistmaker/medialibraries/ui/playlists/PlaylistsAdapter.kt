package com.example.playlistmaker.medialibraries.ui.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistsAdapter(
    private val clickListener: PlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlists = ArrayList<Playlist>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size


    fun updateData(newPlaylistsList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylistsList)
        notifyDataSetChanged()
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}
