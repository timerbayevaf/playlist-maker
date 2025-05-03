package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.util.Creator
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
  private lateinit var settingsInteractor: SettingsInteractor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    settingsInteractor = Creator.provideSettingsInteractor(applicationContext)

    val themeSwitcher = findViewById<SwitchMaterial>(R.id.switchTheme)
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      finish()
    }

    // Обработчик нажатия на кнопку «Поделиться»
    findViewById<TextView>(R.id.share_button).setOnClickListener {
      shareAppLink()
    }

    // Обработчик нажатия на кнопку «Написать в поддержку»
    findViewById<TextView>(R.id.support_button).setOnClickListener {
      sendSupportEmail()
    }

    // Обработчик нажатия на кнопку «Пользовательское соглашение»
    findViewById<TextView>(R.id.user_agreement).setOnClickListener {
      openUserAgreement()
    }

    // Настройка switch "Темная тема"
    themeSwitcher.isChecked = settingsInteractor.getDarkThemeState()
    themeSwitcher.setOnCheckedChangeListener { _, checked ->
      (application as? App)?.setAppTheme(checked)
    }
  }

  private fun shareAppLink() {
    val shareText = getString(R.string.share_text)
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    startActivity(shareIntent)
  }

  private fun sendSupportEmail() {
    val email = getString(R.string.email)
    val subject = getString(R.string.email_subject)
    val body = getString(R.string.email_body)

    val sendSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:")
      putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
      putExtra(Intent.EXTRA_SUBJECT, subject)
      putExtra(Intent.EXTRA_TEXT, body)
    }

    startActivity(sendSupportIntent)

  }

  private fun openUserAgreement() {
    val url = getString(R.string.url_user_agreement)
    val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    startActivity(userAgreementIntent)
  }
}