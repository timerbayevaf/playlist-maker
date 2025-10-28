package com.example.playlistmaker.medialibraries.ui.playlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.audioplayer.ui.AudioPlayerFragment
import com.example.playlistmaker.databinding.MedialibrariesFragmentPlaylistsBinding
import com.example.playlistmaker.medialibraries.ui.detailed.MedialibrariesDetailedPlaylistFragment
import com.example.playlistmaker.medialibraries.utils.PlaylistState
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.mappers.toUI
import com.example.playlistmaker.search.data.mappers.toUI
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MediaLibrariesPlaylistsFragment: Fragment() {
  companion object {
    private const val CLICK_DEBOUNCE_DELAY = 1000L
    fun newInstance() = MediaLibrariesPlaylistsFragment()
  }

  private var _binding: MedialibrariesFragmentPlaylistsBinding? = null
  private val binding get() = _binding!!
  private val viewModel by activityViewModel<MediaLibrariesPlaylistsViewModel>()
  private lateinit var onTrackClickDebounce: (track: Playlist) -> Unit

  private var adapter: PlaylistsAdapter? =  PlaylistsAdapter {   playlist ->
    onTrackClickDebounce(playlist)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    _binding = MedialibrariesFragmentPlaylistsBinding.inflate(inflater, container, false)

    onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlist ->
      Log.d("PlaylistClick", "Clicked playlist: ${playlist.name}")

      val bundle = Bundle().apply {
        putParcelable(MedialibrariesDetailedPlaylistFragment.ARG_PLAYLIST, playlist.toUI())
      }
      findNavController().navigate(R.id.action_mediaLibrariesFragment_to_medialibrariesFragmentDetailedPlaylist, bundle)

    }
    binding.recyclerViewPlaylists.adapter = adapter

    binding.newPlaylist.setOnClickListener {
      findNavController().navigate(R.id.action_mediaLibrariesFragment_to_medialibrariesFragmentCreatePlaylist, null)
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val layoutManager = GridLayoutManager(context, 2)
    binding.recyclerViewPlaylists.layoutManager = layoutManager

    viewModel.stateLiveData.observe(viewLifecycleOwner) {
      render(it)
    }

    viewModel.loadPlaylists()

    setFragmentResultListener("playlist_created") { _, _ ->
      viewModel.loadPlaylists()
      Log.e("MediaLibrariesPlaylistsFragment","playlist_created")
    }
  }

  private fun render(state: PlaylistState) {
    when (state) {
      is PlaylistState.Content -> showContent(state.playlist)
      is PlaylistState.Empty -> showEmpty()
      is PlaylistState.Loading -> {}
    }
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

    adapter?.updateData(playlists)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}