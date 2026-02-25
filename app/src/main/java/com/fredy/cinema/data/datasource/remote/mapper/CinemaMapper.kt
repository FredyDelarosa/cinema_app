package com.fredy.cinema.data.datasource.remote.mapper

import com.fredy.cinema.data.datasource.remote.models.RoomDto
import com.fredy.cinema.data.datasource.remote.models.SeatDto
import com.fredy.cinema.data.datasource.local.entity.RoomEntity
import com.fredy.cinema.data.datasource.local.entity.SeatEntity
import com.fredy.cinema.data.datasource.local.entity.SyncStatus
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.model.SeatStatus

fun RoomDto.toDomain(): Room = Room(
    id = id,
    movie = movie,
    posterUrl = posterUrl,
    time = time,
    price = price,
    createdAt = createdAt
)

fun SeatDto.toDomain(): Seat = Seat(
    id = id,
    roomId = roomId,
    row = row,
    number = number,
    status = SeatStatus.valueOf(status),
    userId = userId,
    version = version,
    isLocalChange = false
)

fun RoomDto.toEntity(): RoomEntity = RoomEntity(
    id = id,
    movie = movie,
    posterUrl = posterUrl,
    time = time,
    price = price,
    createdAt = createdAt
)

fun SeatDto.toEntity(): SeatEntity = SeatEntity(
    id = id,
    roomId = roomId,
    row = row,
    number = number,
    status = status,
    userId = userId,
    version = version,
    syncStatus = SyncStatus.SYNCED
)

fun RoomEntity.toDomain(): Room = Room(
    id = id,
    movie = movie,
    posterUrl = posterUrl,
    time = time,
    price = price,
    createdAt = createdAt
)

fun SeatEntity.toDomain(): Seat = Seat(
    id = id,
    roomId = roomId,
    row = row,
    number = number,
    status = SeatStatus.valueOf(status),
    userId = userId,
    version = version,
    isLocalChange = syncStatus != SyncStatus.SYNCED
)