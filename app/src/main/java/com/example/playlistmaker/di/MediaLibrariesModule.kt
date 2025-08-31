package com.example.playlistmaker.di

import com.example.playlistmaker.medialibraries.ui.MediaLibrariesFavoriteTracksViewModel
import com.example.playlistmaker.medialibraries.ui.MediaLibrariesPlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibrariesModule = module {
  viewModel {
    MediaLibrariesFavoriteTracksViewModel()
  }
  viewModel {
    MediaLibrariesPlaylistsViewModel()
  }
}