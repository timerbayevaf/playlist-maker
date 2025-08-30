package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.SearchHistoryStorage
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorageImpl(
  private val sharedPreferences: SharedPreferences,
  private val gson: Gson
):
  SearchHistoryStorage {
  companion object {
    const val HISTORY = "history"
    private const val MAX_HISTORY_SIZE = 10
  }

  override fun saveToHistory(track: Track) {
     val currentHistory = getHistory()

     // Удаляем трек если уже есть (чтобы не было дубликатов)
     currentHistory.removeAll { it.trackId == track.trackId }

     // Добавляем в начало списка
     currentHistory.add(0, track)

     // Ограничиваем размер истории
     if (currentHistory.size > MAX_HISTORY_SIZE) {
       currentHistory.removeAt(currentHistory.size - 1)
     }

     // Сохраняем обновленный список
     createJsonFromTracksList(sharedPreferences, currentHistory)
  }

  override fun getHistory(): ArrayList<Track> {
     var historyList = ArrayList<Track>()
     val json = sharedPreferences.getString(HISTORY, null)

     if (!json.isNullOrEmpty()) {
       val sType = object : TypeToken<ArrayList<Track>>() {}.type
       historyList = gson.fromJson(json, sType)
     }

     return historyList
  }

  override fun clearHistoryList() {
    createJsonFromTracksList(sharedPreferences, ArrayList())
  }

  private fun createJsonFromTracksList(sharedPreferences: SharedPreferences, tracksList: ArrayList<Track>) {
    val json = gson.toJson(tracksList)
    sharedPreferences.edit()
      .putString(HISTORY, json)
      .apply()
  }
}