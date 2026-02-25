package com.fredy.cinema.data.datasource.local.dao

import androidx.room.*
import com.fredy.cinema.data.datasource.local.entity.SeatEntity
import com.fredy.cinema.data.datasource.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatDao {

    @Query("SELECT * FROM seats WHERE roomId = :roomId ORDER BY row, number")
    fun getSeatsByRoom(roomId: String): Flow<List<SeatEntity>>

    @Query("SELECT * FROM seats WHERE id = :seatId")
    suspend fun getSeatById(seatId: String): SeatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSeats(seats: List<SeatEntity>)

    @Update
    suspend fun updateSeat(seat: SeatEntity)

    @Query("""
        UPDATE seats 
        SET status = :status, 
            userId = :userId, 
            version = :version, 
            syncStatus = :syncStatus,
            lastUpdated = :timestamp
        WHERE id = :seatId
    """)
    suspend fun updateSeatStatus(
        seatId: String,
        status: String,
        userId: String?,
        version: Int,
        syncStatus: SyncStatus,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM seats WHERE roomId = :roomId")
    suspend fun deleteByRoom(roomId: String)

    @Query("DELETE FROM seats")
    suspend fun deleteAll()
}