package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.TrackAdapter
import com.example.playlistmaker.track.TrackApi
import com.example.playlistmaker.track.TrackResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import android.util.Log

class SearchActivity : AppCompatActivity() {
  companion object {
    const val ITUNES_BASE_URL = "https://itunes.apple.com"
  }

  private val retrofit = Retrofit.Builder()
    .baseUrl(ITUNES_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  private val itunesService = retrofit.create(TrackApi::class.java)
  private var trackList = ArrayList<Track>()
  private var historyTrackList = ArrayList<Track>()
  private var trackAdapter: TrackAdapter? = null

  private lateinit var placeholder: LinearLayout
  private lateinit var placeholderNoConnection: ImageView
  private lateinit var placeholderNothingFound: ImageView
  private lateinit var placeholderError: TextView
  private lateinit var searchEditText: EditText
  private lateinit var clearButton: ImageButton
  private lateinit var updateButton: Button
  private lateinit var searchHistoryTitle: TextView
  private lateinit var searchHistoryClearButton: Button
  private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
  private var searchQuery: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)

    // Инициализация истории поиска
    historyTrackList = SearchHistory.getHistory()

    placeholder = findViewById(R.id.placeholder)
    placeholderNoConnection = findViewById(R.id.no_connection_image)
    placeholderNothingFound = findViewById(R.id.nothing_found_image)
    placeholderError  = findViewById(R.id.error_message)

    searchEditText = findViewById(R.id.search_edit_text)
    updateButton = findViewById(R.id.button_update)
    clearButton = findViewById(R.id.clear_button)

    searchHistoryTitle = findViewById(R.id.search_history_title)
    searchHistoryClearButton = findViewById(R.id.search_history_clear_button)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      val displayIntent = Intent(this, MainActivity::class.java)
      startActivity(displayIntent)
    }

    // кнопка очистить историю поиска
    searchHistoryClearButton.setOnClickListener {
      SearchHistory.clearHistoryList()
      historyTrackList.clear()
      setGoneHistoryElement()
      trackAdapter?.notifyDataSetChanged()
    }

    // Автофокус и вызов клавиатуры
    searchEditText.requestFocus()
    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

    setupSearchFieldBehavior()

    trackAdapter = TrackAdapter()
    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
    recyclerView.adapter = trackAdapter
    trackAdapter!!.trackList = trackList

    // Проверка условий для отображения истории при открытии активности
    if (searchEditText.text.isEmpty() && searchEditText.hasFocus() && historyTrackList.isNotEmpty()) {
      showHistory()
    }
  }

  private fun showMessage(text: String, additionalMessage: String) {
    if (text.isNotEmpty()) {
      placeholder.visibility = View.VISIBLE
      placeholderNoConnection.visibility = View.GONE
      placeholderNothingFound.visibility = View.VISIBLE
      trackList.clear()
      trackAdapter?.notifyDataSetChanged()
      placeholderError.text = text

      if (additionalMessage.isNotEmpty()) {
        placeholderNothingFound.visibility = View.GONE
        placeholderNoConnection.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
      } else {
        updateButton.visibility = View.GONE
      }
    } else {
      placeholder.visibility = View.GONE
    }
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

    // очистить поиск
    clearButton.setOnClickListener {
      searchEditText.text.clear()
      imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
      trackList.clear()
      placeholder?.visibility = View.GONE
      showHistory()
      trackAdapter?.notifyDataSetChanged()
    }

    // обновить поиск
    updateButton.setOnClickListener {
      performSearch()
    }

    // обработка нажатия на клавишу
    searchEditText.setOnEditorActionListener { _, actionId, _ ->
      handleSearchAction(actionId)
    }

    // фокусирование на вводе текста
    searchEditText.setOnFocusChangeListener { _, hasFocus ->
      focusVisibility(hasFocus)
    }
  }

  private fun focusVisibility(hasFocus: Boolean) {
    if (hasFocus && searchEditText.text.isEmpty() && historyTrackList.isNotEmpty()) {
      searchHistoryTitle.visibility = View.VISIBLE
      searchHistoryClearButton.visibility = View.VISIBLE
    } else {
      setGoneHistoryElement()
    }
    trackAdapter?.trackList = historyTrackList
    trackAdapter?.notifyDataSetChanged()
  }

  private fun showHistory() {
    searchHistoryTitle.visibility = View.VISIBLE
    searchHistoryClearButton.visibility = View.VISIBLE
    historyTrackList = SearchHistory.getHistory()
    trackAdapter?.trackList = historyTrackList
    trackAdapter?.notifyDataSetChanged()
  }

  private fun setGoneHistoryElement() {
    searchHistoryTitle.visibility = View.GONE
    searchHistoryClearButton.visibility = View.GONE
  }

  private fun updateClearButtonVisibility(text: CharSequence?) {
    if (text.isNullOrEmpty()) {
      clearButton.visibility = ImageButton.GONE
    } else  {
      clearButton.visibility = ImageButton.VISIBLE
      setGoneHistoryElement()
      trackAdapter?.trackList = trackList
    }
  }

  private fun handleSearchAction(actionId: Int): Boolean {
    if (actionId == EditorInfo.IME_ACTION_DONE) {
      performSearch()
      return true
    }
    return false
  }

  private fun performSearch() {
    Log.d("Search", "Search: $searchQuery")

    if (searchQuery.isNotEmpty()) {

      itunesService.search(searchQuery).enqueue(object : Callback<TrackResponse> {
        override fun onResponse(call: Call<TrackResponse>,
                                response: Response<TrackResponse>
        ) {
          if (response.code() == 200) {
            trackList.clear()
            response.body()?.results?.let { results ->
              if (results.isNotEmpty()) {
                trackList.addAll(results)
                trackAdapter?.notifyDataSetChanged()
              } else {
                showMessage(getString(R.string.nothing_found), "")
              }
            }
          } else {
            showMessage(getString(R.string.something_went_wrong), response.code().toString())
          }
        }

        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
          showMessage(getString(R.string.something_went_wrong), t.message.toString())
        }

      })
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("SEARCH_QUERY", searchQuery)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    searchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
    searchEditText.setText(searchQuery)

    if (searchQuery.isEmpty() && searchEditText.hasFocus() && historyTrackList.isNotEmpty()) {
      showHistory()
    }
  }
}