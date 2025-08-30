package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
  private lateinit var binding: ActivitySettingsBinding
  private val viewModel by viewModel<SettingsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySettingsBinding.inflate(layoutInflater)
    setContentView(binding.root)


    binding.apply {
      toolbar.setNavigationOnClickListener { finish() }

      // Обработчик нажатия на кнопку «Поделиться»
      shareButton.setOnClickListener { shareAppLink() }

      // Обработчик нажатия на кнопку «Написать в поддержку»
      supportButton.setOnClickListener { sendSupportEmail() }

      // Обработчик нажатия на кнопку «Пользовательское соглашение»
      userAgreement.setOnClickListener { openUserAgreement() }

      // Настройка switch "Темная тема"
      viewModel.getDarkThemeLiveData().observe(this@SettingsActivity) { isDark ->
        switchTheme.isChecked = isDark
        (application as? App)?.setAppTheme(isDark)
      }
      switchTheme.setOnCheckedChangeListener { _, checked ->
        viewModel.switchTheme(checked)
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