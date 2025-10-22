package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.favorite.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.favorite.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.medialibraries.ui.MediaLibrariesPlaylistsViewModel
import com.example.playlistmaker.medialibraries.ui.favorite.MediaLibrariesFavoriteTracksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibrariesModule = module {
  single {
    Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
      .build()
  }

  single { TrackDbConvertor() }


  single<FavoriteTracksInteractor> {
    FavoriteTracksInteractorImpl(get())
  }

  single<FavoriteTracksRepository> {
    FavoriteTracksRepositoryImpl(get(), get()) // <-- второй get() = TrackDbConvertor
  }

  viewModel {
    MediaLibrariesFavoriteTracksViewModel(get())
  }
  viewModel {
    MediaLibrariesPlaylistsViewModel()
  }

}