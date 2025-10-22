package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.favorite.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { TrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
}