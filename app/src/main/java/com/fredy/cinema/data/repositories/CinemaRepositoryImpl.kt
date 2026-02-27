package com.fredy.cinema.data.repositories

import com.fredy.cinema.core.di.IoDispatcher
import com.fredy.cinema.core.session.UserSession
import com.fredy.cinema.data.datasource.local.dao.RoomDao
import com.fredy.cinema.data.datasource.local.dao.SeatDao
import com.fredy.cinema.data.datasource.local.entity.SyncStatus
import com.fredy.cinema.data.datasource.remote.api.CinemaApi
import com.fredy.cinema.data.datasource.remote.mapper.toDomain
import com.fredy.cinema.data.datasource.remote.mapper.toEntity
import com.fredy.cinema.data.datasource.remote.models.SelectSeatRequest
import com.fredy.cinema.data.datasource.remote.websocket.WebSocketManager
import com.fredy.cinema.data.datasource.remote.websocket.models.WebSocketMessage
import com.fredy.cinema.data.datasource.remote.websocket.models.WebSocketMessageType
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.model.SeatStatus
import com.fredy.cinema.domain.repository.CinemaRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemaRepositoryImpl @Inject constructor(
    private val api: CinemaApi,
    private val roomDao: RoomDao,
    private val seatDao: SeatDao,
    private val webSocketManager: WebSocketManager,
    private val userSession: UserSession,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CinemaRepository {

    private val repositoryScope = CoroutineScope(ioDispatcher + SupervisorJob())

    init {
        observeWebSocketAndSync()
    }

    private fun observeWebSocketAndSync() {
        repositoryScope.launch {
            webSocketManager.messages.collect { message ->
                if (message.type == WebSocketMessageType.SEAT_UPDATED) {
                    val payloadJson = gson.toJson(message.payload)
                    try {
                        val seatDto = gson.fromJson(payloadJson, com.fredy.cinema.data.datasource.remote.models.SeatDto::class.java)
                        // Only update if it's not a local change or if the version is newer
                        val localSeat = seatDao.getSeatById(seatDto.id)
                        if (localSeat == null || seatDto.version >= localSeat.version) {
                            seatDao.insertAllSeats(listOf(seatDto.toEntity()))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override suspend fun getRooms(forceRefresh: Boolean): Result<List<Room>> =
        withContext(ioDispatcher) {
            try {
                if (forceRefresh) {
                    val response = api.getRooms()
                    roomDao.deleteAll()
                    roomDao.insertAllRooms(response.rooms.map { it.toEntity() })
                }
                Result.success(emptyList()) // Flow will provide the data
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
                    seatDao.deleteByRoom(roomId)
                    seatDao.insertAllSeats(response.seats.map { it.toEntity() })
                }
                webSocketManager.connect(roomId, userSession.getUserId())
                Result.success(emptyList())
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

            try {
                // Optimistic UI: Update local DB as PENDING
                if (currentSeat != null) {
                    seatDao.updateSeatStatus(
                        seatId = seatId,
                        status = SeatStatus.SELECTED.name,
                        userId = userId,
                        version = currentSeat.version,
                        syncStatus = SyncStatus.PENDING
                    )
                }

                val response = api.selectSeat(
                    SelectSeatRequest(
                        seatId = seatId,
                        userId = userId,
                        roomId = roomId
                    )
                )

                if (response.success) {
                    // Success, the WebSocket will eventually send an update, 
                    // but we can mark it as SYNCED here if we want immediate feedback.
                    Result.success(Unit)
                } else {
                    // Rollback on conflict (409 handled by Retrofit usually throws or we check response)
                    rollbackSeat(currentSeat)
                    Result.failure(Exception(response.message ?: "Conflict selecting seat"))
                }
            } catch (e: Exception) {
                rollbackSeat(currentSeat)
                Result.failure(e)
            }
        }

    private suspend fun rollbackSeat(originalSeat: com.fredy.cinema.data.datasource.local.entity.SeatEntity?) {
        if (originalSeat != null) {
            seatDao.updateSeatStatus(
                seatId = originalSeat.id,
                status = originalSeat.status,
                userId = originalSeat.userId,
                version = originalSeat.version,
                syncStatus = SyncStatus.SYNCED
            )
        }
    }

    override suspend fun releaseSeat(seatId: String, userId: String, roomId: String): Result<Unit> =
        withContext(ioDispatcher) {
            val currentSeat = seatDao.getSeatById(seatId)
            try {
                val response = api.releaseSeat(
                    SelectSeatRequest(
                        seatId = seatId,
                        userId = userId,
                        roomId = roomId
                    )
                )

                if (response.success) {
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