package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl (
  private val sharedPreferences: SharedPreferences
) : SettingsRepository {
  companion object {
    private const val DARK_THEME_KEY = "dark_theme_enabled"
  }

  override fun getDarkThemeEnabled(): Boolean {
    return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
  }

  override fun setDarkThemeEnabled(enabled: Boolean) {
    sharedPreferences.edit()
      .putBoolean(DARK_THEME_KEY, enabled)
      .apply()
  }
}
