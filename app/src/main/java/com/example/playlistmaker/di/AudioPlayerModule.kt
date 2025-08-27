package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.audioplayer.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerRepository
import com.example.playlistmaker.audioplayer.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.audioplayer.ui.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioPlayerModule = module {

  single {
    MediaPlayer()
  }

  single<AudioPlayerRepository> {
    AudioPlayerRepositoryImpl(mediaPlayer = get())
  }

  single<AudioPlayerInteractor> {
    AudioPlayerInteractorImpl(repository = get())
  }

  viewModel {
    AudioPlayerViewModel(get())
  }
}