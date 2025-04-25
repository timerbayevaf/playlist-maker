package com.example.playlistmaker.presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "PlayerActivity"
    private const val DELAY = 1000L
  }

  private lateinit var play: ImageButton
  private lateinit var trackTime: TextView
  private lateinit var imagePlayer: ImageView
  private lateinit var trackName: TextView
  private lateinit var artistName: TextView
  private lateinit var albumContent: TextView
  private lateinit var albumTittle: TextView
  private lateinit var durationContent: TextView
  private lateinit var yearContent: TextView
  private lateinit var genreContent: TextView
  private lateinit var countryContent: TextView

  private val audioPlayerInteractor: AudioPlayerInteractor = Creator.provideAudioPlayerInteractor()
  private val handler = Handler(Looper.getMainLooper())
  private lateinit var updateTimerRunnable: Runnable

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_player)

    initViews()
    setupToolbar()
    updateTimerRunnable = createUpdateTimerRunnable()

    // Отключаем кнопку до подготовки
    play.isEnabled = false

    val track = getTrackFromIntent()
    track?.let { setupTrackViews(it) } ?: run {
      Log.e(TAG, "Track data not available")
      finish()
    }
  }

  private fun initViews() {
    play = findViewById(R.id.play_pause_button)
    trackTime = findViewById(R.id.trackTime)
    imagePlayer = findViewById(R.id.albumArt)
    trackName = findViewById(R.id.trackName)
    artistName = findViewById(R.id.artistName)
    albumContent = findViewById(R.id.albumContent)
    albumTittle = findViewById(R.id.albumTittle)
    durationContent = findViewById(R.id.durationContent)
    yearContent = findViewById(R.id.yearContent)
    genreContent = findViewById(R.id.genreContent)
    countryContent = findViewById(R.id.countryContent)
  }


  private fun setupToolbar() {
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      finish()
    }
  }

  // Получаем объект Track из Intent
  private fun getTrackFromIntent(): Track? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(Track.TRACK, Track::class.java)
    } else {
      @Suppress("DEPRECATION")
      intent.getParcelableExtra(Track.TRACK)
    }
  }

  private fun setupTrackViews(track: Track) {
    // настройка текста
    trackTime.text = getText(R.string.player_default_time)
    trackName.text = track.trackName
    artistName.text = track.artistName
    durationContent.text = getFormattedTrackTime(track.trackTimeMillis)
    yearContent.text = track.getFormattedReleaseYear()
    genreContent.text = track.primaryGenreName
    countryContent.text = track.country

    setupAlbumInfo(track.collectionName)
    // Загрузка обложки
    loadAlbumArt(track.getCoverArtwork())

    // Подготовка плеера
    track.previewUrl?.takeIf { it.isNotEmpty() }?.let { url ->
      preparePlayer(url)
    } ?: run {
      play.isEnabled = false
      Log.e(TAG,"Preview not available")
    }
    play.setOnClickListener {
      playbackControl()
    }
  }

  private fun setupAlbumInfo(collectionName: String?) {
    if (collectionName.isNullOrEmpty()) {
      albumContent.visibility = View.GONE
      albumTittle.visibility = View.GONE
    } else {
      albumContent.text = collectionName
      albumContent.visibility = View.VISIBLE
      albumTittle.visibility = View.VISIBLE
    }
  }

  private fun loadAlbumArt(coverUrl: String?) {
    Glide.with(this)
      .load(coverUrl)
      .placeholder(R.drawable.track_palceholder)
      .centerCrop()
      .transform(RoundedCorners(2))
      .into(imagePlayer)
  }


  private fun preparePlayer(url: String) {
    audioPlayerInteractor.preparePlayer(url) {state ->
      when (state) {
        PlayerState.PREPARED -> {
          play.isEnabled = true
          handler.post(updateTimerRunnable)
//          trackTime.setText(getString(R.string.player_default_time))
        }
        else -> Unit
      }

    }
  }

  private fun startPlayer() {
    audioPlayerInteractor.startPlayer()
    setPlayPauseIcon(true)
    handler.post(updateTimerRunnable)
  }

  private fun pausePlayer() {
    audioPlayerInteractor.pausePlayer()
    setPlayPauseIcon(false)
    handler.removeCallbacks(updateTimerRunnable)
  }

  private fun playbackControl() {
    audioPlayerInteractor.switchedStatePlayer { state ->
      when (state) {
        PlayerState.PLAYING -> startPlayer()
        PlayerState.PAUSED -> pausePlayer()
        else -> Unit
      }
    }
  }

  private fun setPlayPauseIcon(isPlaying: Boolean) {
    val isDarkTheme = (applicationContext as App).darkTheme
    val iconRes = when {
      isPlaying && !isDarkTheme -> R.drawable.pause_light
      isPlaying -> R.drawable.pause_night
      !isDarkTheme -> R.drawable.play_light
      else -> R.drawable.play_night
    }
    play.setImageResource(iconRes)
  }

  private fun createUpdateTimerRunnable(): Runnable {
    return object : Runnable {
      override fun run() {
        trackTime.text = getFormattedTrackTime(audioPlayerInteractor.getPosition())
        handler.postDelayed(this, DELAY)
      }
    }
  }


  override fun onPause() {
    super.onPause()
    handler.removeCallbacks(updateTimerRunnable)
  }

  override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacks(updateTimerRunnable)
    audioPlayerInteractor.stopPlayer()
  }
}