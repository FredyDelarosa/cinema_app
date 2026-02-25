package com.fredy.cinema.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey
    val id: String,
    val movie: String,
    val posterUrl: String?,
    val time: String,
    val price: Double,
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
