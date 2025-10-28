package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.db.convertor.TrackDbConvertor
import com.example.playlistmaker.db.convertor.TrackInPlaylistDbConvertor
import com.example.playlistmaker.favorite.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.favorite.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.medialibraries.ui.playlists.MediaLibrariesPlaylistsViewModel
import com.example.playlistmaker.medialibraries.ui.favorite.MediaLibrariesFavoriteTracksViewModel
import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.playlist.ui.PlaylistCreateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibrariesModule = module {
  factory { TrackDbConvertor() }

  single<FavoriteTracksRepository> {
    FavoriteTracksRepositoryImpl(get(), get())
  }

  factory { PlaylistDbConvertor() }

  single { get<Context>().contentResolver }

  factory { TrackInPlaylistDbConvertor() }

  factory {
    Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  single<FavoriteTracksInteractor> {
    FavoriteTracksInteractorImpl(get())
  }

  single<PlaylistRepository> {
    PlaylistRepositoryImpl(get(), get(), get())
  }

  single<PlaylistInteractor> {
    PlaylistInteractorImpl(get())
  }

  viewModel {
    PlaylistCreateViewModel(androidContext(), get())
  }

  viewModel {
    MediaLibrariesFavoriteTracksViewModel(get())
  }
  viewModel {
    MediaLibrariesPlaylistsViewModel(get())
  }

}