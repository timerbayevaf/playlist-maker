package com.example.playlistmaker.domain.api

interface SettingsRepository {
  fun getDarkThemeEnabled(): Boolean
  fun setDarkThemeEnabled(enabled: Boolean)
}
