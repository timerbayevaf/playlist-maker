package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App

class SettingsViewModel(
  private val settingsInteractor: SettingsInteractor,
  private val application: App
) : ViewModel() {
  private val darkThemeLiveData = MutableLiveData<Boolean>()
  fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

  init {
    darkThemeLiveData.postValue(settingsInteractor.getDarkThemeState())
  }

  fun switchTheme(isDark: Boolean) {
    if (settingsInteractor.getDarkThemeState() != isDark) {
      settingsInteractor.updateDarkThemeState(isDark)
      darkThemeLiveData.postValue(isDark)
      application.setAppTheme(isDark)
    }
  }
}
