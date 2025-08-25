package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.tracks.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.tracks.domain.api.TracksInteractor
import com.example.playlistmaker.tracks.domain.api.TracksRepository
import com.example.playlistmaker.audioplayer.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.audioplayer.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.tracks.domain.impl.TracksInteractorImpl

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
