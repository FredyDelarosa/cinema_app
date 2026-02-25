package com.fredy.cinema.data.di

import com.fredy.cinema.core.di.CinemaRetrofit
import com.fredy.cinema.data.datasource.remote.api.CinemaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CinemaNetworkModule {

    @Provides
    @Singleton
    fun provideCinemaApi(
        @CinemaRetrofit retrofit: Retrofit
    ): CinemaApi {
        return retrofit.create(CinemaApi::class.java)
    }
}