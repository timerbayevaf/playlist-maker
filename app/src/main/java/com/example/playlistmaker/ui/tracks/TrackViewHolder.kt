package com.example.playlistmaker.ui.tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
    .inflate(R.layout.track_view, parent, false)) {

    // Уникальный идентификатор трека
//    private val trackId: TextView = itemView.findViewById(R.id.trackId)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = getFormattedTrackTime(model.trackTimeMillis)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_palceholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
    }

}