package com.example.playlistmaker.util

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
  private val PREFERENCES = "practicum_example_preferences"
  private var mediaPlayer = MediaPlayer()

  private fun getTracksRepository(): TracksRepository {
    return TracksRepositoryImpl(RetrofitNetworkClient())
  }

  fun provideTracksInteractor(): TracksInteractor {
    return TracksInteractorImpl(getTracksRepository())
  }

  fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
    return AudioPlayerInteractorImpl(AudioPlayerRepositoryImpl(mediaPlayer))
  }

  fun provideSettingsInteractor(context: Context): SettingsInteractor {
    val sharedPreferences = context.getSharedPreferences(
      "app_settings",
      Context.MODE_PRIVATE
    )
    return SettingsInteractorImpl(
      SettingsRepositoryImpl(sharedPreferences)
    )
  }
}
