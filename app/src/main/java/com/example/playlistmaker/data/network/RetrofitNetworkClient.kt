package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
  companion object {
    const val ITUNES_BASE_URL = "https://itunes.apple.com"
  }

  private val retrofit = Retrofit.Builder()
    .baseUrl(ITUNES_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  private val itunesService = retrofit.create(TrackApi::class.java)

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