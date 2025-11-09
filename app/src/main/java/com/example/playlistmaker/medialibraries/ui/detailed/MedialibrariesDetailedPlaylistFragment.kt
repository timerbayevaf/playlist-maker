package com.example.playlistmaker.medialibraries.ui.detailed

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.audioplayer.ui.AudioPlayerFragment
import com.example.playlistmaker.databinding.MedialibrariesFragmentDetailedPlaylistBinding
import com.example.playlistmaker.medialibraries.utils.DetailedTracksState
import com.example.playlistmaker.medialibraries.utils.ShareState
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.mappers.toDomain
import com.example.playlistmaker.playlist.mappers.toUI
import com.example.playlistmaker.playlist.presentation.models.PlaylistUI
import com.example.playlistmaker.search.data.mappers.toUI
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MedialibrariesDetailedPlaylistFragment: Fragment() {
    companion object {
        private const val TAG = "MedialibrariesFragmentDetailedPlaylist"
        const val ARG_PLAYLIST = "playlist_arg"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: MedialibrariesFragmentDetailedPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var onTrackClickDebounce: (track: Track) -> Unit
    private lateinit var onTrackClickLongDebounce: (track: Track) -> Unit
    private val viewModel by viewModel<MedialibrariesDetailedPlaylistViewModel>()
    private var currentPlaylist: Playlist? = null
    private val adapter by lazy {
        PlaylistWithTrackAdapter (
            onTrackClick = { track -> onTrackClickDebounce(track) },
            onTrackLongClick = { track -> onTrackClickLongDebounce(track) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPlaylist = getPlaylistFromArgs()
        Log.d(TAG, "Track received: $currentPlaylist")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MedialibrariesFragmentDetailedPlaylistBinding.inflate(inflater, container, false)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            track.let {
                val bundle = Bundle().apply {
                    putParcelable(AudioPlayerFragment.ARG_TRACK, track.toUI())
                }

                findNavController().navigate(
                    R.id.action_medialibrariesFragmentDetailedPlaylist_to_audioPlayerFragment,
                    bundle
                )

            }
        }
        onTrackClickLongDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            showDeleteTrackConfirmationDialog(track)
        }

        binding.recycleViewBottomSheet.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPlaylist?.let {
            viewModel.loadPlaylistWithTracks(it)
        }


        setupToolbar()
        setupBackPressedHandler()
        setupBottomSheet()

        viewModel.screenStateLiveData.observe(viewLifecycleOwner) {state ->
            renderTracksState(state.tracksState)
            renderShare(state.shareState)
            bindPlaylist(state.playlist, state.totalDuration )
        }

        val layoutManager = LinearLayoutManager(context)
        binding.recycleViewBottomSheet.layoutManager = layoutManager

        binding.dimOverlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.iconMenu.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            renderSheet()
        }

        binding.deletePlaylist.setOnClickListener {
            showDeletePlaylistConfirmationDialog()
        }

        binding.editPlaylist.setOnClickListener {
            currentPlaylist?.let {
                val bundle = Bundle().apply {
                    putParcelable(ARG_PLAYLIST, it.toUI())
                }
                findNavController().navigate(R.id.action_medialibrariesFragmentDetailedPlaylist_to_playlistEditFragment, bundle)
            }
        }

        setFragmentResultListener("playlist_edited") { _, _ ->
            currentPlaylist?.let { viewModel.loadPlaylistById(it.id) }
        }
    }


    private fun renderSheet() {
        currentPlaylist?.let {
            loadAlbumArt(it.imageUrl,binding.playlistCoverImageSheet)
            binding.numberOfTracksSheet.text = context?.resources?.getQuantityString(
                R.plurals.playlist_count_tracks,
                it.countTracks,
                it.countTracks
            )
            binding.playlistNameSheet.text = it.name
        }
    }

    private fun renderShare(state: ShareState) {
        when (state) {
            is ShareState.Empty -> showToast( getString(R.string.no_tracks_to_share))
            is ShareState.Content -> shareText(state.message)
            else -> {}
        }
    }

    private fun shareText(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        val chooser = Intent.createChooser(intent, getString(R.string.playlist_details_share))

        try {
            startActivity(chooser)
        } catch (e: Exception) {
            showToast(getString(R.string.no_tracks_to_share))
        }
    }

    private fun renderTracksState (state: DetailedTracksState) {
        when (state) {
            is DetailedTracksState.Content -> showContent(state.tracks)
            is DetailedTracksState.Empty -> showEmpty()
            is DetailedTracksState.Default -> {}
            is DetailedTracksState.Removed -> findNavController().navigateUp()
        }
    }

    private fun showEmpty() {
        binding.recycleViewBottomSheet.visibility = View.GONE
        binding.messageEmptyList.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recycleViewBottomSheet.visibility = View.VISIBLE
        adapter.updateData(tracks)
    }

    private fun showDeleteTrackConfirmationDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message, track.trackName))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                deleteTrackFromPlaylist(track)
            }
            .show()
    }

    private fun showDeletePlaylistConfirmationDialog() {
        currentPlaylist?.let { it ->
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setTitle(getString(R.string.playlist_details_delete))
                .setMessage(getString(R.string.want_to_delete_playlist, it.name))
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deletePlaylist(it)
                }
                .show()
        }
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        currentPlaylist?.let { playlist ->
            viewModel.removeTrackFromPlaylist(playlist.id, track)
        }
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheetMenuDetails
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding?.let {
                    it.dimOverlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding?.let {
                    it.dimOverlay.visibility = View.VISIBLE
                    it.dimOverlay.isClickable = true
                    it.recycleViewBottomSheet.isNestedScrollingEnabled = false
                }
            }
        })


        binding.iconShare.setOnClickListener {
            shareTracksIfAvailable()
        }

        binding.sharePlaylist.setOnClickListener {
            shareTracksIfAvailable()
        }

        binding.dimOverlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.iconMenu.post {
            val iconBottom = binding.iconMenu.bottom
            val screenHeight = resources.displayMetrics.heightPixels
            val peekHeight = screenHeight - iconBottom - 90.dpToPx()
            BottomSheetBehavior.from(binding.standardBottomSheet).peekHeight = peekHeight
        }
    }

    private fun handleBackPressed() {
        findNavController().navigateUp()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }
    }

    private fun shareTracksIfAvailable() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        currentPlaylist?.let { playlist ->
            val prefix = getString(R.string.share_playlist_title_prefix)
            viewModel.sharePlaylist(playlist, prefix)
        }
    }

    private fun bindPlaylist(newPlaylist: Playlist?, totalDuration: Int) {
        newPlaylist?.let {
            binding.playlistName.text = newPlaylist.name
            val hasDescription = !newPlaylist.description.isNullOrBlank()
            if (hasDescription) {
                binding.playlistDetails.text = newPlaylist.description
                binding.playlistDetails.visibility = View.VISIBLE
            } else {
                binding.playlistDetails.visibility = View.GONE
            }
            binding.playlistTracks.text = context?.resources?.getQuantityString(
                R.plurals.playlist_count_tracks,
                newPlaylist.countTracks,
                newPlaylist.countTracks
            )
            binding.playlistMinutes.text = formatPlaylistDuration(totalDuration)

            loadAlbumArt(newPlaylist.imageUrl, binding.playlistImage)
        }
    }


    private fun loadAlbumArt(coverUrl: String?, view: ImageView) {
        Glide.with(this)
            .load(coverUrl)
            .placeholder(R.drawable.track_placeholder)
            .transform(CenterCrop(), RoundedCorners(2))
            .into(view)
    }

    private fun formatPlaylistDuration(totalDuration: Int): String {
        return context?.resources?.getQuantityString(
            R.plurals.playlist_total_minutes,
            totalDuration,
            totalDuration
        ) ?: ""
    }

    private fun getPlaylistFromArgs(): Playlist? {
        val playlistUI: PlaylistUI? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_PLAYLIST, PlaylistUI::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_PLAYLIST)
        }
        return playlistUI?.toDomain()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}