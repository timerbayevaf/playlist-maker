package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SettingsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
  private var _binding: SettingsFragmentBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<SettingsViewModel>()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    _binding = SettingsFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupClickListeners()
    setupThemeSwitchObserver()
  }

  private fun setupClickListeners() {
    binding.apply {
      switchTheme.setOnCheckedChangeListener { _, isChecked ->
        viewModel.switchTheme(isChecked)
      }
      shareButton.setOnClickListener { shareAppLink() }
      supportButton.setOnClickListener { sendSupportEmail() }
      userAgreement.setOnClickListener { openUserAgreement() }
    }
  }

  private fun setupThemeSwitchObserver() {
    viewModel.getDarkThemeLiveData().observe(viewLifecycleOwner) { isChecked ->
      if (binding.switchTheme.isChecked != isChecked) {
        binding.switchTheme.isChecked = isChecked
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
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