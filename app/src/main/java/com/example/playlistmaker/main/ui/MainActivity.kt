package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
    val navController = navHostFragment.navController

    binding.bottomNavigationView.setupWithNavController(navController)

    navController.addOnDestinationChangedListener { _, destination, _ ->
      if (
        destination.id == R.id.audioPlayerFragment
        || destination.id == R.id.medialibrariesFragmentCreatePlaylist
        || destination.id == R.id.medialibrariesFragmentDetailedPlaylist
        || destination.id == R.id.playlistEditFragment) {
        binding.bottomNavigationView.visibility = android.view.View.GONE
      } else {
        binding.bottomNavigationView.visibility = android.view.View.VISIBLE
      }

    }
  }
}