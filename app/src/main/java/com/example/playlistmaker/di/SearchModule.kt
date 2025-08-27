package com.example.playlistmaker.di

import android.app.Application
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryStorage
import com.example.playlistmaker.search.data.SearchHistoryStorageImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient.Companion.ITUNES_BASE_URL
import com.example.playlistmaker.search.data.network.TrackApi
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

  single<TrackApi> {
    Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(TrackApi::class.java)
  }

  single<NetworkClient> {
    RetrofitNetworkClient(itunesService = get())
  }

  single<SearchHistoryStorage> {
    SearchHistoryStorageImpl(sharedPreferences = get())
  }

  single<SearchHistoryRepository> {
    SearchHistoryRepositoryImpl(networkClient = get(), storage = get())
  }

  single<SearchHistoryInteractor> {
    SearchHistoryInteractorImpl(repository = get())
  }

  viewModel {
    SearchViewModel(searchHistoryInteractor = get(), application = androidContext() as Application)
  }
}