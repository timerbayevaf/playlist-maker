package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.audioPlayerModule
import com.example.playlistmaker.di.mediaLibrariesModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class App : Application(), KoinComponent {
  companion object {
    fun getFormattedTrackTime(millis: Long): String {
      return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }
  }

  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@App)
      modules(listOf(audioPlayerModule, searchModule, settingModule, mediaLibrariesModule))
    }

    // Применение темы
    applyTheme()
  }

  private fun applyTheme() {
    val settingsInteractor: SettingsInteractor = get()
    val isDarkTheme = settingsInteractor.getDarkThemeState()
    AppCompatDelegate.setDefaultNightMode(
      if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
      else AppCompatDelegate.MODE_NIGHT_NO
    )
  }

  fun setAppTheme(isDark: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
      if (isDark) AppCompatDelegate.MODE_NIGHT_YES
      else AppCompatDelegate.MODE_NIGHT_NO
    )
  }


}