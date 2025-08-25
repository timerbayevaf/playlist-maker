package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.Track

class TrackViewHolder(private  val binding: TrackViewBinding): RecyclerView.ViewHolder(
    binding.root) {

    fun bind(model: Track) {
        binding.apply {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = getFormattedTrackTime(model.trackTimeMillis)
        }

        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_palceholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(binding.trackImage)
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}