package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
  fun getDarkThemeEnabled(): Boolean
  fun setDarkThemeEnabled(enabled: Boolean)
}
