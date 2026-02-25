package com.fredy.cinema.data.di

import com.fredy.cinema.data.datasource.local.CinemaDatabase
import com.fredy.cinema.data.datasource.local.dao.RoomDao
import com.fredy.cinema.data.datasource.local.dao.SeatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CinemaDatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDao(database: CinemaDatabase): RoomDao = database.roomDao()

    @Provides
    @Singleton
    fun provideSeatDao(database: CinemaDatabase): SeatDao = database.seatDao()
}