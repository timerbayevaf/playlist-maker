package com.example.playlistmaker.medialibraries.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMedialibrariesBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibrariesActivity: AppCompatActivity() {
  private lateinit var binding: ActivityMedialibrariesBinding
  private lateinit var tabMediator: TabLayoutMediator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMedialibrariesBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.viewPager.adapter = MediaLibrariesViewPagerAdapter(supportFragmentManager, lifecycle)

    tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
      when(position) {
        0 -> tab.text = getString(R.string.media_favorite_tracks_title)
        1 -> tab.text = getString(R.string.media_playlist_title)
      }
    }
    tabMediator.attach()

    binding.toolbar.setNavigationOnClickListener {
      finish()
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    tabMediator?.detach()
  }
}