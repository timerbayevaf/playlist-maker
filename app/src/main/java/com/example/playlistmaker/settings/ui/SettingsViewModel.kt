package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(
  private val settingsInteractor: SettingsInteractor
) : ViewModel() {
  private val darkThemeLiveData = MutableLiveData<Boolean>()
  fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

  init {
    darkThemeLiveData.value = settingsInteractor.getDarkThemeState()
  }

  fun switchTheme(isDark: Boolean) {
    settingsInteractor.updateDarkThemeState(isDark)
    darkThemeLiveData.value = isDark
  }
}
