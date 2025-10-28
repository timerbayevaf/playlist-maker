package com.example.playlistmaker.medialibraries.ui.detailed

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class PlaylistWithTrackAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
): RecyclerView.Adapter<PlaylistWithTrackViewHolder> ()  {
    var trackList = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistWithTrackViewHolder =
        PlaylistWithTrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistWithTrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onTrackClick(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            onTrackLongClick(trackList[position])
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTrackList: List<Track>) {
        trackList.clear()
        trackList.addAll(newTrackList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = trackList.size
}