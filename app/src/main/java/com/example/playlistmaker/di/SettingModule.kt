package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingModule = module {
  single<SharedPreferences> {
    androidContext()
      .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
  }

  single<SettingsRepository> {
    SettingsRepositoryImpl(sharedPreferences = get())
  }

  factory< SettingsInteractor> {
    SettingsInteractorImpl(settingsRepository = get())
  }

  viewModel {
    SettingsViewModel(settingsInteractor = get(), application = androidApplication() as App)
  }

}