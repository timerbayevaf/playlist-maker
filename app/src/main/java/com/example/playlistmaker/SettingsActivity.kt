package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_settings)

    val backButton = findViewById<ImageButton>(R.id.back)
    backButton.setOnClickListener {
      val displayIntent = Intent(this, MainActivity::class.java)
      startActivity(displayIntent)
    }
  }
}