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

class SearchActivity : AppCompatActivity() {

  private lateinit var searchEditText: EditText
  private lateinit var clearButton: ImageButton
  private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
  private var searchQuery: String = ""

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