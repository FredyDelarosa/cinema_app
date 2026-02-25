package com.fredy.cinema.domain.repository

import com.fredy.cinema.data.datasource.remote.websocket.models.WebSocketMessage
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.model.Seat
import kotlinx.coroutines.flow.Flow

interface CinemaRepository {

    suspend fun getRooms(forceRefresh: Boolean = false): Result<List<Room>>
    fun observeRooms(): Flow<List<Room>>

    suspend fun getSeats(roomId: String, forceRefresh: Boolean = false): Result<List<Seat>>
    fun observeSeats(roomId: String): Flow<List<Seat>>

    suspend fun selectSeat(seatId: String, userId: String, roomId: String): Result<Unit>
    suspend fun releaseSeat(seatId: String, userId: String, roomId: String): Result<Unit>

    fun observeWebSocketMessages(): Flow<WebSocketMessage>
}