package com.fredy.cinema.core.di

import android.content.Context
import androidx.room.Room
import com.fredy.cinema.data.datasource.local.CinemaDatabase
import com.fredy.cinema.data.datasource.local.dao.RoomDao
import com.fredy.cinema.data.datasource.local.dao.SeatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCinemaDatabase(
        @ApplicationContext context: Context
    ): CinemaDatabase {
        return Room.databaseBuilder(
            context,
            CinemaDatabase::class.java,
            "cinema_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideRoomDao(db: CinemaDatabase): RoomDao = db.roomDao()

    @Provides
    @Singleton
    fun provideSeatDao(db: CinemaDatabase): SeatDao = db.seatDao()
}