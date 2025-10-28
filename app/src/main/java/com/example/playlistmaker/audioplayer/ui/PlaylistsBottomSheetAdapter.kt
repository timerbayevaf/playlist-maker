package com.example.playlistmaker.audioplayer.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistsBottomSheetAdapter(
    private val clickListener: PlaylistClickListener
) : RecyclerView.Adapter<PlaylistViewBottomSheetHolder>() {
    private val playlists = ArrayList<Playlist>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewBottomSheetHolder =
        PlaylistViewBottomSheetHolder.from(parent)


    override fun onBindViewHolder(holder: PlaylistViewBottomSheetHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPlaylistsList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylistsList)
        notifyDataSetChanged()
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}
