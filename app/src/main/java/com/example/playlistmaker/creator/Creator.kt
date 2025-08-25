package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.audioplayer.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.audioplayer.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchHistoryStorage
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl

object Creator {
  private val PREFERENCES = "practicum_example_preferences"
  private var mediaPlayer = MediaPlayer()

  private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
    val sharedPreferences = context.getSharedPreferences(
      SearchHistoryStorage.HISTORY, Context.MODE_PRIVATE)
    return SearchRepositoryImpl(RetrofitNetworkClient(),SearchHistoryStorage(sharedPreferences))
  }


  fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
    return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
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
