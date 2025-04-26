package com.example.playlistmaker.util

import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
  private fun getTracksRepository(): TracksRepository {
    return TracksRepositoryImpl(RetrofitNetworkClient())
  }

  fun provideTracksInteractor(): TracksInteractor {
    return TracksInteractorImpl(getTracksRepository())
  }

  fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
    return AudioPlayerInteractorImpl(AudioPlayerRepositoryImpl())
  }
}
