package com.example.playlistmaker.track

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.SearchHistory.addTrackInHistoryList

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder> () {
  var trackList = ArrayList<Track>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

  override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
    holder.bind(trackList[position])
    holder.itemView.setOnClickListener {
      addTrackInHistoryList(trackList[position])
    }
  }

  override fun getItemCount(): Int = trackList.size
}