package com.example.playlistmaker.medialibraries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.MedialibrariesFragmentFavoriteTracksBinding

class MediaLibrariesFavoriteTracksFragment : Fragment() {

  private var _binding: MedialibrariesFragmentFavoriteTracksBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    _binding = MedialibrariesFragmentFavoriteTracksBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // пока логики нет
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    fun newInstance() = MediaLibrariesFavoriteTracksFragment()
  }
}
