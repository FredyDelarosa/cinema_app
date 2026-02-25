package com.fredy.cinema.data.repositories

import com.fredy.cinema.core.di.IoDispatcher
import com.fredy.cinema.data.datasource.local.dao.RoomDao
import com.fredy.cinema.data.datasource.local.dao.SeatDao
import com.fredy.cinema.data.datasource.local.entity.SyncStatus
import com.fredy.cinema.data.datasource.remote.api.CinemaApi
import com.fredy.cinema.data.datasource.remote.mapper.toDomain
import com.fredy.cinema.data.datasource.remote.mapper.toEntity
import com.fredy.cinema.data.datasource.remote.models.SelectSeatRequest
import com.fredy.cinema.data.datasource.remote.websocket.WebSocketManager
import com.fredy.cinema.data.datasource.remote.websocket.models.WebSocketMessage
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.model.SeatStatus
import com.fredy.cinema.domain.repository.CinemaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemaRepositoryImpl @Inject constructor(
    private val api: CinemaApi,
    private val roomDao: RoomDao,
    private val seatDao: SeatDao,
    private val webSocketManager: WebSocketManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CinemaRepository {

    override suspend fun getRooms(forceRefresh: Boolean): Result<List<Room>> =
        withContext(ioDispatcher) {
            try {
                if (forceRefresh) {
                    val response = api.getRooms()
                    val rooms = response.rooms.map { it.toDomain() }

                    roomDao.deleteAll()
                    roomDao.insertAllRooms(response.rooms.map { it.toEntity() })

                    Result.success(rooms)
                } else {
                    val rooms = roomDao.getAllRooms().map { entities ->
                        entities.map { it.toDomain() }
                    }
                    Result.success(emptyList())
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun observeRooms(): Flow<List<Room>> =
        roomDao.getAllRooms().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getSeats(roomId: String, forceRefresh: Boolean): Result<List<Seat>> =
        withContext(ioDispatcher) {
            try {
                if (forceRefresh) {
                    val response = api.getSeatsByRoom(roomId)
                    val seats = response.seats.map { it.toDomain() }

                    seatDao.deleteByRoom(roomId)
                    seatDao.insertAllSeats(response.seats.map { it.toEntity() })

                    webSocketManager.connect(roomId, "current-user-id")

                    Result.success(seats)
                } else {
                    Result.success(emptyList())
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun observeSeats(roomId: String): Flow<List<Seat>> =
        seatDao.getSeatsByRoom(roomId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun selectSeat(seatId: String, userId: String, roomId: String): Result<Unit> =
        withContext(ioDispatcher) {
            val currentSeat = seatDao.getSeatById(seatId)

            if (currentSeat != null) {
                seatDao.updateSeatStatus(
                    seatId = seatId,
                    status = SeatStatus.SELECTED.name,
                    userId = userId,
                    version = currentSeat.version,
                    syncStatus = SyncStatus.PENDING
                )
            }

            try {
                val response = api.selectSeat(
                    SelectSeatRequest(
                        seatId = seatId,
                        userId = userId,
                        roomId = roomId
                    )
                )

                if (response.success) {
                    seatDao.updateSeatStatus(
                        seatId = seatId,
                        status = SeatStatus.SELECTED.name,
                        userId = userId,
                        version = currentSeat?.version?.plus(1) ?: 1,
                        syncStatus = SyncStatus.SYNCED
                    )
                    Result.success(Unit)
                } else {
                    if (currentSeat != null) {
                        seatDao.updateSeatStatus(
                            seatId = seatId,
                            status = currentSeat.status,
                            userId = currentSeat.userId,
                            version = currentSeat.version,
                            syncStatus = SyncStatus.SYNCED
                        )
                    }
                    Result.failure(Exception(response.message ?: "Error selecting seat"))
                }
            } catch (e: Exception) {
                if (currentSeat != null) {
                    seatDao.updateSeatStatus(
                        seatId = seatId,
                        status = currentSeat.status,
                        userId = currentSeat.userId,
                        version = currentSeat.version,
                        syncStatus = SyncStatus.SYNCED
                    )
                }
                Result.failure(e)
            }
        }

    override suspend fun releaseSeat(seatId: String, userId: String, roomId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val response = api.releaseSeat(
                    SelectSeatRequest(
                        seatId = seatId,
                        userId = userId,
                        roomId = roomId
                    )
                )

                if (response.success) {
                    val seatsResponse = api.getSeatsByRoom(roomId)
                    seatDao.deleteByRoom(roomId)
                    seatDao.insertAllSeats(seatsResponse.seats.map { it.toEntity() })

                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun observeWebSocketMessages(): Flow<WebSocketMessage> =
        webSocketManager.messages
}