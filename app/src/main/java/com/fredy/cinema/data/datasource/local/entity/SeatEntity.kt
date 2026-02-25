package com.fredy.cinema.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

enum class SyncStatus {
    SYNCED,
    PENDING,
    CONFLICT
}

@Entity(
    tableName = "seats",
    indices = [Index(value = ["roomId", "row", "number"], unique = true)]
)
data class SeatEntity(
    @PrimaryKey
    val id: String,
    val roomId: String,
    val row: String,
    val number: Int,
    val status: String,
    val userId: String?,
    val version: Int,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastUpdated: Long = System.currentTimeMillis()
)