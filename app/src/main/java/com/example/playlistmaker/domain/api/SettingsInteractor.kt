package com.example.playlistmaker.domain.api

interface SettingsInteractor {
  fun getDarkThemeState(): Boolean
  fun updateDarkThemeState(enabled: Boolean)
}
