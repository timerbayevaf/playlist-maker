package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.track.Track

class PlayerActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_player)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      finish()
    }

    // Получаем объект Track из Intent
    val track = intent.getParcelableExtra(Track.TRACK, Track::class.java)

    if (track != null) {
      val imagePlayer = findViewById<ImageView>(R.id.albumArt)
      val trackName = findViewById<TextView>(R.id.trackName)
      val artistName = findViewById<TextView>(R.id.artistName)
      val trackTime = findViewById<TextView>(R.id.trackTime)
      val albumContent = findViewById<TextView>(R.id.albumContent)
      val albumTittle = findViewById<TextView>(R.id.albumTittle)
      val durationContent = findViewById<TextView>(R.id.durationContent)
      val yearContent = findViewById<TextView>(R.id.yearContent)
      val genreContent = findViewById<TextView>(R.id.genreContent)
      val countryContent = findViewById<TextView>(R.id.countryContent)

      trackName.text = track.trackName
      artistName.text = track.artistName
      // заглушка
      trackTime.text = "00:30"

      if (track.collectionName.isNullOrEmpty()) {
        albumContent.visibility = View.GONE
        albumTittle.visibility = View.GONE
      } else {
        albumContent.text = track.collectionName
      }

      durationContent.text = track.getFormattedTrackTime()
      yearContent.text = track.getFormattedReleaseYear()
      genreContent.text = track.primaryGenreName
      countryContent.text = track.country

      Glide.with(imagePlayer)
        .load(track.getCoverArtwork())
        .placeholder(R.drawable.track_palceholder)
        .centerCrop()
        .transform(RoundedCorners(2))
        .into(imagePlayer)
    }
  }
}