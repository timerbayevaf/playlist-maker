package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SearchHistory {
  private const val SIZE = 10
  private const val HISTORY = "SearchHistory"
  private val sharedPrefs: SharedPreferences
    get() = App.getAppContext().getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE)

  fun getHistory(): ArrayList<Track> {
    var historyList = ArrayList<Track>()
    val json = sharedPrefs.getString(HISTORY, null)

    if (!json.isNullOrEmpty()) {
      val sType = object : TypeToken<ArrayList<Track>>() {}.type
      historyList = Gson().fromJson(json, sType)
    }

    return historyList
  }

  fun addTrackInHistoryList(track: Track) {
    val historyList = getHistory()

    if (historyList.contains(track)) {
      historyList.remove(track)
    }

    if (historyList.size >= SIZE) {
      historyList.removeAt(historyList.size - 1)
    }

    historyList.add(0, track)
    createJsonFromTracksList(sharedPrefs, historyList)
  }

  fun clearHistoryList() {
    createJsonFromTracksList(sharedPrefs, ArrayList())
  }

  private fun createJsonFromTracksList(sharedPreferences: SharedPreferences, tracksList: ArrayList<Track>) {
    val json = Gson().toJson(tracksList)
    sharedPreferences.edit()
      .putString(HISTORY, json)
      .apply()
  }
}