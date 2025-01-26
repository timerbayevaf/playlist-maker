package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.view.View

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val searchButton = findViewById<Button>(R.id.search)
    searchButton.setOnClickListener {
      val displayIntent = Intent(this, SearchActivity::class.java)
      startActivity(displayIntent)
    }

    val mediaButton = findViewById<Button>(R.id.media_library)
    mediaButton.setOnClickListener {
      val displayIntent = Intent(this, MediaLibrariesActivity::class.java)
      startActivity(displayIntent)
    }

    val settingsButton = findViewById<Button>(R.id.settings)
    settingsButton.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View?) {
        val displayIntent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(displayIntent)
      }
    })

  }
}