package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.util.Log
import android.widget.ProgressBar
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.AudioPlayerActivity
import com.example.playlistmaker.ui.tracks.TrackAdapter
import com.example.playlistmaker.util.Creator

class SearchActivity : AppCompatActivity() {
  companion object {
    const val ITUNES_BASE_URL = "https://itunes.apple.com"
    private const val CLICK_DEBOUNCE_DELAY = 1000L
    private const val SEARCH_DEBOUNCE_DELAY = 2000L
  }
  private val tracksInteractor: TracksInteractor = Creator.provideTracksInteractor()

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
  private lateinit var progressBar: ProgressBar
  private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
  private var searchQuery: String = ""

  // Debounce
  private var isClickAllowed = true
  private val handler = Handler(Looper.getMainLooper())
  private val searchRunnable = Runnable { performSearch() }

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

    progressBar = findViewById(R.id.progress_bar)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    toolbar.setNavigationOnClickListener {
      finish()
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

    trackAdapter = TrackAdapter {
      SearchHistory.addTrackInHistoryList(it)
      if (clickDebounce()) {
        // Создаем Intent для перехода на PlayerActivity
        val intent = Intent(this, AudioPlayerActivity::class.java)
          .apply { putExtra(Track.TRACK, it) }
        startActivity(intent)
      }
    }

    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
    recyclerView.adapter = trackAdapter
    trackAdapter!!.trackList = trackList

    // Проверка условий для отображения истории при открытии активности
    if (searchEditText.text.isEmpty() && searchEditText.hasFocus() && historyTrackList.isNotEmpty()) {
      showHistory()
    }
  }

  private fun showMessage(text: String, isNetworkError: Boolean) {
    if (text.isNotEmpty()) {
      placeholder.visibility = View.VISIBLE
      placeholderNoConnection.visibility = View.GONE
      placeholderNothingFound.visibility = View.VISIBLE
      trackList.clear()
      trackAdapter?.notifyDataSetChanged()
      placeholderError.text = text

      if (isNetworkError) {
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
        searchDebounce()
        searchQuery = s?.toString() ?: ""
        updateVisibility(s)
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

  private fun updateVisibility(text: CharSequence?) {
    if (text.isNullOrEmpty()) {
      clearButton.visibility = ImageButton.GONE
      // Если запрос пуст, скрываем результаты поиска и показываем историю (если она есть)
      trackList.clear()
      trackAdapter?.notifyDataSetChanged()
      if (historyTrackList.isNotEmpty()) {
        showHistory()
      } else {
        setGoneHistoryElement()
      }
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
      progressBar.visibility = View.VISIBLE
      placeholder.visibility = View.GONE
      setGoneHistoryElement()

      tracksInteractor.searchTracks(searchQuery, object : TracksInteractor.TracksConsumer {
        override fun consume(foundTracks: List<Track>) {
          handler.post {
            progressBar.visibility = View.GONE
            trackList.clear()
            trackList.addAll(foundTracks)
            trackAdapter?.notifyDataSetChanged()

            if (trackList.isEmpty()) {
              showMessage(getString(R.string.nothing_found), false)
            }
          }
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

  private fun clickDebounce() : Boolean {
    val current = isClickAllowed
    if (isClickAllowed) {
      isClickAllowed = false
      handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
    }
    return current
  }

  private fun searchDebounce() {
    handler.removeCallbacks(searchRunnable)
    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
  }
}