package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

  private lateinit var searchEditText: EditText
  private lateinit var clearButton: ImageButton
  private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
  private var searchQuery: String = ""
  private var trackList =listOf(
    Track(
    trackName = "Smells Like Teen Spirit",
    artistName = "Nirvana",
    trackTime = "5:01",
    artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
  ),
    Track(
      trackName = "Billie Jean",
      artistName = "Michael Jackson",
      trackTime = "4:35",
      artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
    ),
    Track(
      trackName = "Stayin' Alive",
      artistName = "Bee Gees",
      trackTime = "4:10",
      artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
    ),
    Track(
      trackName = "Whole Lotta Love",
      artistName = "Led Zeppelin",
      trackTime = "5:33",
      artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
    ),
    Track(
      trackName = "Sweet Child O'Mine",
      artistName = "Guns N' Roses",
      trackTime = "5:03",
      artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
    ))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      val displayIntent = Intent(this, MainActivity::class.java)
      startActivity(displayIntent)
    }

    searchEditText = findViewById(R.id.search_edit_text)
    clearButton = findViewById(R.id.clearButton)

    // Автофокус и вызов клавиатуры
    searchEditText.requestFocus()
    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

    setupSearchFieldBehavior()

    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
    val trackAdapter = TrackAdapter(trackList)
    recyclerView.adapter = trackAdapter
  }

  private fun setupSearchFieldBehavior() {
    searchEditText.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchQuery = s?.toString() ?: ""
        updateClearButtonVisibility(s)
      }

      override fun afterTextChanged(s: Editable?) {}
    })

    clearButton.setOnClickListener {
      searchEditText.text.clear()
      imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    searchEditText.setOnEditorActionListener { _, actionId, _ ->
      handleSearchAction(actionId)
    }
  }

  private fun updateClearButtonVisibility(text: CharSequence?) {
    clearButton.visibility = if (text.isNullOrEmpty()) ImageButton.GONE else ImageButton.VISIBLE
  }

  private fun handleSearchAction(actionId: Int): Boolean {
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
      performSearch()
      return true
    }
    return false
  }

  private fun performSearch() {
    val query = searchEditText.text.toString()
    Log.d("Search", "Search: $query")
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("SEARCH_QUERY", searchQuery)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    searchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
    searchEditText.setText(searchQuery)
  }
}