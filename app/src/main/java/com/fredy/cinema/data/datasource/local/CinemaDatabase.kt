package com.fredy.cinema.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fredy.cinema.data.datasource.local.dao.RoomDao
import com.fredy.cinema.data.datasource.local.dao.SeatDao
import com.fredy.cinema.data.datasource.local.entity.RoomEntity
import com.fredy.cinema.data.datasource.local.entity.SeatEntity
import com.fredy.cinema.data.datasource.local.entity.SyncStatus

class Converters {
    @androidx.room.TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @androidx.room.TypeConverter
    fun toSyncStatus(status: String): SyncStatus = SyncStatus.valueOf(status)
}

@Database(
    entities = [RoomEntity::class, SeatEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CinemaDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun seatDao(): SeatDao
}