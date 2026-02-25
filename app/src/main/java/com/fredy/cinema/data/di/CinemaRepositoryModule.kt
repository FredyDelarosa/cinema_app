package com.fredy.cinema.data.di

import com.fredy.cinema.data.repositories.CinemaRepositoryImpl
import com.fredy.cinema.domain.repository.CinemaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CinemaRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCinemaRepository(
        cinemaRepositoryImpl: CinemaRepositoryImpl
    ): CinemaRepository
}