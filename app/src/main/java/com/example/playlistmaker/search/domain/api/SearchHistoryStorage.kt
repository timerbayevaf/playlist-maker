package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryStorage {
   fun saveToHistory(track: Track)

   fun getHistory(): ArrayList<Track>

  fun clearHistoryList()
}