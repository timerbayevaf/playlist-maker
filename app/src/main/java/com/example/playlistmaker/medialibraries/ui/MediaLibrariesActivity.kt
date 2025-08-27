package com.example.playlistmaker.medialibraries.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMedialibrariesBinding

class MediaLibrariesActivity: AppCompatActivity() {
  private lateinit var binding: ActivityMedialibrariesBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMedialibrariesBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }
}