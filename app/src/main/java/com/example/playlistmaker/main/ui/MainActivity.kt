package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.View
import com.example.playlistmaker.medialibraries.ui.MediaLibrariesActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.search.setOnClickListener {
      val displayIntent = Intent(this, SearchActivity::class.java)
      startActivity(displayIntent)
    }

    binding.mediaLibrary.setOnClickListener {
      val displayIntent = Intent(this, MediaLibrariesActivity::class.java)
      startActivity(displayIntent)
    }

    binding.settings.setOnClickListener {
      val displayIntent = Intent(this, SettingsActivity::class.java)
      startActivity(displayIntent)
    }

  }
}