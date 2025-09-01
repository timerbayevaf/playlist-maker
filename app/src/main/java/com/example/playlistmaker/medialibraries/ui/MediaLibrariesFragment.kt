package com.example.playlistmaker.medialibraries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MedialibrariesFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibrariesFragment: Fragment() {
  private var _binding: MedialibrariesFragmentBinding? = null
  private val binding get() = _binding!!
  private var tabMediator: TabLayoutMediator? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = MedialibrariesFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.viewPager.adapter = MediaLibrariesViewPagerAdapter(childFragmentManager, lifecycle)

    tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
      when(position) {
        0 -> tab.text = getString(R.string.media_favorite_tracks_title)
        1 -> tab.text = getString(R.string.media_playlist_title)
      }
    }
    tabMediator?.attach()

  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onDestroy() {
    super.onDestroy()
    tabMediator?.detach()
  }
}