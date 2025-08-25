package com.example.playlistmaker.audioplayer.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "PlayerActivity"
  }
  private lateinit var settingsInteractor: SettingsInteractor
  private lateinit var viewModel: AudioPlayerViewModel
  private lateinit var binding: ActivityPlayerBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPlayerBinding.inflate(layoutInflater)
    setContentView(binding.root)

    settingsInteractor = Creator.provideSettingsInteractor(applicationContext)

    setupToolbar()

    viewModel = ViewModelProvider(
      this, AudioPlayerViewModel.getViewModelFactory()
    )[AudioPlayerViewModel::class.java]

    val track = getTrackFromIntent()
    track?.let { setupTrackViews(it) } ?: run {
      Log.e(TAG, "Track data not available")
      finish()
    }

    viewModel.getStatePlayerLiveData().observe(this) {state ->
      when(state) {
        PlayerState.PAUSED -> setPlayPauseIcon(false)
        PlayerState.PLAYING -> setPlayPauseIcon(true)
        PlayerState.PREPARED, PlayerState.DEFAULT -> {
          setPlayPauseIcon(false)
          binding.trackTime.text = getString(R.string.player_default_time)
        }
      }
    }

    viewModel.getCurrentTimeLiveData().observe(this) { time ->
      binding.trackTime.text = getFormattedTrackTime(time)
    }

  }

  private fun setupToolbar() {
    binding.toolbar.setNavigationOnClickListener {
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
    binding.apply {
      trackTime.text = getText(R.string.player_default_time)
      trackName.text = track.trackName
      artistName.text = track.artistName
      durationContent.text = getFormattedTrackTime(track.trackTimeMillis)
      yearContent.text = track.getFormattedReleaseYear()
      genreContent.text = track.primaryGenreName
      countryContent.text = track.country
    }

    setupAlbumInfo(track.collectionName)
    // Загрузка обложки
    loadAlbumArt(track.getCoverArtwork())

    // Подготовка плеера
    track.previewUrl?.takeIf { it.isNotEmpty() }?.let { url ->
      viewModel.preparePlayer(url)
    } ?: run {
      binding.playOrPause.isEnabled = false
      Log.e(TAG,"Preview not available")
    }
    binding.playOrPause.setOnClickListener {
      viewModel.changePlayerState()
    }
  }

  private fun setupAlbumInfo(collectionName: String?) {
    if (collectionName.isNullOrEmpty()) {
      binding.apply {
        albumContent.visibility = View.GONE
        albumTittle.visibility = View.GONE
      }
    } else {
      binding.apply {
        albumContent.text = collectionName
        albumContent.visibility = View.VISIBLE
        albumTittle.visibility = View.VISIBLE
      }
    }
  }

  private fun loadAlbumArt(coverUrl: String?) {
    Glide.with(this)
      .load(coverUrl)
      .placeholder(R.drawable.track_palceholder)
      .centerCrop()
      .transform(RoundedCorners(2))
      .into(binding.albumArt)
  }


  private fun setPlayPauseIcon(isPlaying: Boolean) {
    val isDarkTheme = settingsInteractor.getDarkThemeState()
    val iconRes = when {
      isPlaying && !isDarkTheme -> R.drawable.pause_light
      isPlaying -> R.drawable.pause_night
      !isDarkTheme -> R.drawable.play_light
      else -> R.drawable.play_night
    }
    binding.playOrPause.setImageResource(iconRes)
  }

  override fun onStart() {
    super.onStart()
    viewModel.onStart()
  }

  override fun onPause() {
    super.onPause()
    viewModel.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.onDestroy()
  }
  override fun onResume() {
    super.onResume()
    viewModel.onResume()
  }
}
