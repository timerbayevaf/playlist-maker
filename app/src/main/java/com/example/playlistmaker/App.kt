package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.creator.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class App : Application() {
  companion object {
    private var instance: App? = null

    fun getAppContext(): Context {
      return instance?.applicationContext
        ?: throw IllegalStateException("Application not initialized")
    }

    fun getFormattedTrackTime(millis: Long): String {
      return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }
  }
  private lateinit var settingsInteractor: SettingsInteractor

  override fun onCreate() {
    super.onCreate()
    instance = this

    // Инициализация зависимостей
    settingsInteractor = Creator.provideSettingsInteractor(this)

    // Применение темы
    applyTheme()
  }
  private fun applyTheme() {
    val isDarkTheme = settingsInteractor.getDarkThemeState()
    setAppTheme(isDarkTheme)
  }

  fun setAppTheme(isDark: Boolean) {
    settingsInteractor.updateDarkThemeState(isDark)
    AppCompatDelegate.setDefaultNightMode(
      if (isDark) AppCompatDelegate.MODE_NIGHT_YES
      else AppCompatDelegate.MODE_NIGHT_NO
    )
  }
}