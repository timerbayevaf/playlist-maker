package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

  private val executor = Executors.newCachedThreadPool()

  override fun searchMovies(expression: String, consumer: TracksInteractor.MoviesConsumer) {
    executor.execute {
      consumer.consume(repository.searchTracks(expression))
    }
  }
}