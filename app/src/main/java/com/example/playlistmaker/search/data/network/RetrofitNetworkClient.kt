package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: TrackApi): NetworkClient {
  companion object {
    const val ITUNES_BASE_URL = "https://itunes.apple.com"
  }

  override fun doRequest(dto: Any): Response {
    if (dto is TracksSearchRequest) {
      val resp = itunesService.search(dto.expression).execute()

      val body = resp.body() ?: Response()

      return body.apply { resultCode = resp.code() }
    } else {
      return Response().apply { resultCode = 400 }
    }
  }
}