package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      val displayIntent = Intent(this, MainActivity::class.java)
      startActivity(displayIntent)
    }
  }
}