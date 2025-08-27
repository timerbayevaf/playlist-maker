package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

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
