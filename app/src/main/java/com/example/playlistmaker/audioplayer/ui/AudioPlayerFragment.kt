package com.example.playlistmaker.audioplayer.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.databinding.PlayerFragmentBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.example.playlistmaker.search.data.mappers.toDomain
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {
  companion object {
    private const val TAG = "AudioPlayerFragment"
    const val ARG_TRACK = "track_arg"
  }
  private val settingsInteractor: SettingsInteractor by inject()
  private val viewModel by viewModel<AudioPlayerViewModel>()
  private var _binding: PlayerFragmentBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = PlayerFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupToolbar()

    val track = getTrackFromArgs()
    Log.d("AudioPlayerActivity", "Track received: $track")
    track?.let { setupTrackViews(it) } ?: run {
      Log.e(TAG, "Track data not available")
      findNavController().popBackStack()
    }

    viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) {state ->
      setPlayPauseIcon(state.playerState == PlayerState.PLAYING)
      binding.trackTime.text = getFormattedTrackTime(state.currentTime)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun setupToolbar() {
    binding.toolbar.setNavigationOnClickListener {
      findNavController().popBackStack()
    }
  }

  // Получаем объект Track из аргументов фрагмента
  private fun getTrackFromArgs(): Track? {
    val trackUI: TrackUI? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      arguments?.getParcelable(ARG_TRACK, TrackUI::class.java)
    } else {
      @Suppress("DEPRECATION")
      arguments?.getParcelable(ARG_TRACK)
    }
    return trackUI?.toDomain()

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

  override fun onResume() {
    super.onResume()
    viewModel.onResume()
  }
}
