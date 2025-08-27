package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.audioplayer.ui.AudioPlayerActivity
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.data.mappers.toUI
import com.example.playlistmaker.search.presentation.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
  companion object {
    private const val CLICK_DEBOUNCE_DELAY = 1000L
  }

  private val viewModel by viewModel<SearchViewModel>()
  private lateinit var binding: ActivitySearchBinding
  private var trackAdapter = TrackAdapter { startAdapter(it) }
  private var textWatcher: TextWatcher? = null

  // Debounce
  private var isClickAllowed = true
  private val handler = Handler(Looper.getMainLooper())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySearchBinding.inflate(layoutInflater)
    setContentView(binding.root)

    viewModel.getSearchTrackStatusLiveData().observe(this) {
      render(it)
    }

    binding.toolbar.setNavigationOnClickListener {
      finish()
    }

    // Recycler View
    binding.recyclerView.adapter = trackAdapter

    textWatcher = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
      override fun afterTextChanged(s: Editable?) { }
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val currentText = s?.toString() ?: ""

        viewModel.searchDebounce(
          changedText = currentText
        )
        updateClearButtonVisibility(currentText, binding.searchEditText.hasFocus())

      }
    }

    textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

    // кнопка очистить историю поиска
    binding.searchHistoryClearButton.setOnClickListener {
      viewModel.clearHistory()
    }

    // очистить поиск
    binding.clearButton.setOnClickListener {
      viewModel.clearSearchText()
      binding.searchEditText.text.clear()
      updateClearButtonVisibility(binding.searchEditText.text.toString(), false)
    }

    binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
      updateClearButtonVisibility(binding.searchEditText.text.toString(), hasFocus)
      viewModel.focusVisibility()
    }
  }

  private fun render(state: StateSearch) {
    when (state) {
      is StateSearch.Loading -> showLoading()
      is StateSearch.History -> showHistoryList(state.tracks)
      is StateSearch.Content -> showContent(state.tracks)
      is StateSearch.Error -> showError(state.errorMessage)
      is StateSearch.Empty -> showEmpty(state.emptyMessage)
    }
  }

  private fun hideAllView() {
    binding.apply {
      recyclerView.visibility = View.GONE
      searchHistoryTitle.visibility = View.GONE
      placeholder.visibility = View.GONE
      placeholderNoConnection.visibility = View.GONE
      placeholderNothingFound.visibility = View.GONE
      progressBar.visibility = View.GONE
      placeholderError.visibility = View.GONE
      searchHistoryClearButton.visibility = View.GONE
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun showHistoryList(tracksList: List<Track>) {
    hideAllView()
    binding.apply {
      recyclerView.visibility = View.VISIBLE
      searchHistoryTitle.visibility = View.VISIBLE
      searchHistoryClearButton.visibility = View.VISIBLE
      searchHistoryTitle.visibility = View.VISIBLE
    }
    trackAdapter.trackList = tracksList as ArrayList
    trackAdapter.notifyDataSetChanged()
  }

  fun showLoading() {
    hideAllView()
    binding.progressBar.visibility = View.VISIBLE
  }

  @SuppressLint("NotifyDataSetChanged")
  fun showContent(moviesList: List<Track>) {
    hideAllView()
    binding.recyclerView.visibility = View.VISIBLE

    trackAdapter.trackList.clear()
    trackAdapter.trackList.addAll(moviesList)
    trackAdapter.notifyDataSetChanged()
  }

  @SuppressLint("NotifyDataSetChanged")
  fun showError(errorMessage: String) {
    hideAllView()
    binding.apply {
      placeholder.visibility = View.VISIBLE
      placeholderNothingFound.visibility = View.VISIBLE
      placeholderError.text = errorMessage
    }
    trackAdapter.notifyDataSetChanged()
  }

  @SuppressLint("NotifyDataSetChanged")
  fun showEmpty(emptyMessage: String) {
    binding.apply {
      placeholder.visibility = View.VISIBLE
      placeholderError.text = emptyMessage
      placeholderNothingFound.visibility = View.VISIBLE
    }
    trackAdapter.notifyDataSetChanged()
  }

  // обновления видимости кнопки очистки
  private fun updateClearButtonVisibility(text: String, hasFocus: Boolean) {
    val shouldShowClearButton = text.isNotEmpty() || hasFocus
    binding.clearButton.visibility = if (shouldShowClearButton) View.VISIBLE else View.GONE
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel.restoreState()
  }

  private fun clickDebounce() : Boolean {
    val current = isClickAllowed
    if (isClickAllowed) {
      isClickAllowed = false
      handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
    }
    return current
  }

  private fun startAdapter(track: Track) {
    if (clickDebounce()) {
      viewModel.addTrackInHistoryList(track)
      val intent = Intent(this, AudioPlayerActivity::class.java)
        .apply {
          putExtra(TrackUI.TRACK, track.toUI())
        }
      startActivity(intent)
    }
  }
}