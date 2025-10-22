package com.example.playlistmaker.medialibraries.ui.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.audioplayer.ui.AudioPlayerFragment
import com.example.playlistmaker.databinding.MedialibrariesFragmentFavoriteTracksBinding
import com.example.playlistmaker.medialibraries.utils.FavoriteTrackState
import com.example.playlistmaker.search.data.mappers.toUI
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList
import kotlin.getValue

class MediaLibrariesFavoriteTracksFragment : Fragment() {

  companion object {
    private const val CLICK_DEBOUNCE_DELAY = 1000L
    fun newInstance() = MediaLibrariesFavoriteTracksFragment()
  }

  private var _binding: MedialibrariesFragmentFavoriteTracksBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<MediaLibrariesFavoriteTracksViewModel>()
  private lateinit var onTrackClickDebounce: (track: Track) -> Unit
  private var favoriteTrackAdapter = TrackAdapter {   track ->
    onTrackClickDebounce(track)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    _binding = MedialibrariesFragmentFavoriteTracksBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Получить актуальное состояние из LiveData()
    viewModel.observeState().observe(viewLifecycleOwner) {
      render(it)
    }

    // Recycler View
    binding.favouriteTracks.adapter = favoriteTrackAdapter

    // get favorite Tracks from bd
    viewModel.fillData()

    onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
      viewModel.clickDebounce()

      val bundle = Bundle().apply {
        putParcelable(AudioPlayerFragment.ARG_TRACK, track.toUI())
      }

      findNavController().navigate(
        R.id.action_mediaLibrariesFragment_to_audioPlayerFragment,
        bundle
      )
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun render(state: FavoriteTrackState) {
    when (state) {
      is FavoriteTrackState.Loading -> {
        with(binding) {
          progressBar.visibility = View.VISIBLE
          imagePlaceholder.visibility = View.GONE
          textError.visibility = View.GONE
          favouriteTracks.visibility = View.GONE
        }
      }

      is FavoriteTrackState.Content -> {
        favoriteTrackAdapter.trackList = state.tracks as ArrayList
        favoriteTrackAdapter.notifyDataSetChanged()
        with(binding) {
          favouriteTracks.visibility = View.VISIBLE
          imagePlaceholder.visibility = View.GONE
          textError.visibility = View.GONE
          progressBar.visibility = View.GONE
        }
      }

      is FavoriteTrackState.Empty -> {
        with(binding) {
          imagePlaceholder.visibility = View.VISIBLE
          textError.visibility = View.VISIBLE
          favouriteTracks.visibility = View.GONE
          progressBar.visibility = View.GONE
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}