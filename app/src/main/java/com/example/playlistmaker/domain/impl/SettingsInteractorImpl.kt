package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
  private val settingsRepository: SettingsRepository
) : SettingsInteractor {
  override fun getDarkThemeState(): Boolean {
    return settingsRepository.getDarkThemeEnabled()
  }
  override fun updateDarkThemeState(enabled: Boolean) {
    settingsRepository.setDarkThemeEnabled(enabled)
  }
}
