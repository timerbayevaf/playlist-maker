package com.example.playlistmaker

import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
  private fun getMoviesRepository(): TracksRepository {
    return TracksRepositoryImpl(RetrofitNetworkClient())
  }

  fun provideMoviesInteractor(): TracksInteractor {
    return TracksInteractorImpl(getMoviesRepository())
  }

  fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
    return AudioPlayerInteractorImpl(AudioPlayerRepositoryImpl())
  }
}
