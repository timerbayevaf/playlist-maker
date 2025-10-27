package com.example.playlistmaker.audioplayer.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getFormattedTrackTime
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.audioplayer.domain.models.PlayerState
import com.example.playlistmaker.audioplayer.utils.AddToPlaylistStatus
import com.example.playlistmaker.databinding.PlayerFragmentBinding
import com.example.playlistmaker.medialibraries.utils.PlaylistState
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.example.playlistmaker.search.data.mappers.toDomain
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max
import kotlin.math.min

class AudioPlayerFragment : Fragment() {
  companion object {
    private const val TAG = "AudioPlayerFragment"
    const val ARG_TRACK = "track_arg"
    private const val CLICK_DEBOUNCE_DELAY = 1000L
  }
  private val settingsInteractor: SettingsInteractor by inject()
  private val viewModel by viewModel<AudioPlayerViewModel>()
  private var currentTrack: Track? = null
  private var _binding: PlayerFragmentBinding? = null
  private val binding get() = _binding!!
  private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
  private lateinit var onPlaylistClickDebounce: (track: Playlist) -> Unit
  private val adapter by lazy {
    PlaylistsBottomSheetAdapter { playlist ->
      onPlaylistClickDebounce(playlist)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    currentTrack = getTrackFromArgs()
    Log.d(TAG, "Track received: $currentTrack")
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = PlayerFragmentBinding.inflate(inflater, container, false)

    onPlaylistClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlist ->
      currentTrack?.let {
        viewModel.addTrackToPlaylist(it, playlist)
      }
    }

    binding.recyclerViewPlaylists.adapter = adapter

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.loadPlaylists()
    setupToolbar()

    currentTrack?.let { setupTrackViews(it) } ?: run {
      Log.e(TAG, "Track data not available")
      findNavController().popBackStack()
    }

    setupBottomSheet()

    viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) {state ->
      setPlayPauseIcon(state.playerState == PlayerState.PLAYING)
      binding.trackTime.text = getFormattedTrackTime(state.currentTime)
      updateFavoriteIcon(state.isFavorite)
      renderPlaylistState(state.playlistState)
      renderPlaylistStatus(state.addToPlaylistStatus)
    }

    binding.addToFavorites.setOnClickListener {
      currentTrack?.let { viewModel.onFavoriteClicked(track = it)}
    }

    binding.addToPlaylist.setOnClickListener {
      viewModel.loadPlaylists()
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    binding.newPlaylist.setOnClickListener {
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
      val args = Bundle().apply {
        putBoolean("from_player", true)
      }

      findNavController().navigate(R.id.action_audioPlayerFragment_to_medialibrariesFragmentCreatePlaylist, args)
    }

    setFragmentResultListener("playlist_created") { _, bundle ->
      val fromPlayer = bundle.getBoolean("from_player", false)
      if (fromPlayer) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
      }
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

  private fun updateFavoriteIcon(isFavorite: Boolean) {
    val isDarkTheme = settingsInteractor.getDarkThemeState()
    val iconRes = when {
      isFavorite && !isDarkTheme -> R.drawable.favorite_active_light
      isFavorite -> R.drawable.favorite_active_night
      !isDarkTheme -> R.drawable.favorite
      else -> R.drawable.favorite_night
    }
    binding.addToFavorites.setImageResource(iconRes)
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
      viewModel.preparePlayer(url, track)
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
    if (coverUrl.isNullOrEmpty()) {
      binding.albumArt.setImageResource(R.drawable.track_placeholder)
      return
    }

    Glide.with(this)
      .load(coverUrl)
      .placeholder(R.drawable.track_placeholder)
      .transform(CenterCrop(), RoundedCorners(2))
      .into(binding.albumArt)
  }

  private fun setupBottomSheet() {
    val overlay = binding.overlay
    val bottomSheet = binding.playlistsBottomSheet
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
      override fun onStateChanged(bottomSheet: View, newState: Int) {
        overlay.visibility =
          if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
      }

      override fun onSlide(bottomSheet: View, slideOffset: Float) {
        overlay.alpha = max(0f, min(1f, slideOffset))
      }
    })

    overlay.setOnClickListener {
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
  }

  private fun renderPlaylistStatus(state: AddToPlaylistStatus) {
    when (state) {
      is AddToPlaylistStatus.Added -> {
        showToast(getString(R.string.playlist_message_added_to_playlist,state.playlist.name))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.resetAddToPlaylistStatus()
      }

      is AddToPlaylistStatus.Exists -> {
        showToast( getString(R.string.playlist_message_track_exist_in_playlist,state.playlist.name))
        viewModel.resetAddToPlaylistStatus()
      }

      else -> Unit
    }
  }

  private fun showToast(message: String) {
    val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)

    // применяем цвета вручную, если хотим гарантировать одинаковое поведение на старых Android
    val isNightMode = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES

    if (isNightMode) {
      snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
      snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    } else {
      snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
      snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
    snackbar.show()
  }


  private fun renderPlaylistState(state: PlaylistState) {
    when (state) {
      is PlaylistState.Content -> showContent(state.playlist)
      is PlaylistState.Empty -> showEmpty()
      is PlaylistState.Loading -> showLoading()
    }
  }

  private fun showLoading(){
    binding.progressBar.visibility = View.VISIBLE
    binding.recyclerViewPlaylists.visibility = View.GONE
    binding.imagePlaceholder.visibility = View.GONE
    binding.textError.visibility = View.VISIBLE
  }

    private fun showEmpty() {
      binding.recyclerViewPlaylists.visibility = View.GONE
      binding.progressBar.visibility = View.GONE
      binding.imagePlaceholder.visibility = View.VISIBLE
      binding.textError.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
      binding.recyclerViewPlaylists.visibility = View.VISIBLE
      binding.progressBar.visibility = View.GONE
      binding.imagePlaceholder.visibility = View.GONE
      binding.textError.visibility = View.GONE

      Log.d("PlaylistClick", "Clicked playlist: ${playlists.size}")
      adapter.updateData(playlists)
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

  override fun onPause() {
    super.onPause()
    viewModel.onPause()
  }

  override fun onResume() {
    super.onResume()
    viewModel.onResume()
  }
}
