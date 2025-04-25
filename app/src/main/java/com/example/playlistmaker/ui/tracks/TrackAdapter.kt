package com.example.playlistmaker.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.search.SearchHistory.addTrackInHistoryList
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(val clickListener: TrackClickListener):  RecyclerView.Adapter<TrackViewHolder>() {
  var trackList = ArrayList<Track>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

  override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
    holder.bind(trackList[position])
    holder.itemView.setOnClickListener {
      addTrackInHistoryList(trackList[position])

      clickListener.onTrackClick(trackList[position])
    }
  }

  override fun getItemCount(): Int = trackList.size

  fun interface TrackClickListener {
    fun onTrackClick(movie: Track)
  }
}