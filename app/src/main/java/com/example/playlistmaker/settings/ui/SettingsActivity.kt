package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
  private lateinit var settingsInteractor: SettingsInteractor
  private lateinit var binding: ActivitySettingsBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySettingsBinding.inflate(layoutInflater)
    setContentView(binding.root)
    settingsInteractor = Creator.provideSettingsInteractor(applicationContext)


    binding.apply {
      toolbar.setNavigationOnClickListener {
        finish()
      }
      // Обработчик нажатия на кнопку «Поделиться»
      shareButton.setOnClickListener {
        shareAppLink()
      }
      // Обработчик нажатия на кнопку «Написать в поддержку»
      supportButton.setOnClickListener {
        sendSupportEmail()
      }
      // Обработчик нажатия на кнопку «Пользовательское соглашение»
      userAgreement.setOnClickListener {
        openUserAgreement()
      }
      // Настройка switch "Темная тема"
      switchTheme.isChecked = settingsInteractor.getDarkThemeState()
      switchTheme.setOnCheckedChangeListener { _, checked ->
        (application as? App)?.setAppTheme(checked)
      }
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