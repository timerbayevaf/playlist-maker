package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(
  private val settingsInteractor: SettingsInteractor
) : ViewModel() {
  companion object {
    fun getFactory(context: android.content.Context) = viewModelFactory {
      initializer {
        SettingsViewModel(
          Creator.provideSettingsInteractor(context)
        )
      }
    }
  }
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
