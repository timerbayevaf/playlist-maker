package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
  fun getDarkThemeState(): Boolean
  fun updateDarkThemeState(enabled: Boolean)
}
