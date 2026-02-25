package com.fredy.cinema.domain.model

enum class SeatStatus {
    AVAILABLE,
    SELECTED,
    BOOKED
}

data class Seat(
    val id: String,
    val roomId: String,
    val row: String,
    val number: Int,
    val status: SeatStatus,
    val userId: String?,
    val version: Int,
    val isLocalChange: Boolean = false
) {

    fun getDisplayName(): String = "$row$number"

    fun isAvailable(): Boolean = status == SeatStatus.AVAILABLE

    fun isSelectedBy(userId: String): Boolean =
        status == SeatStatus.SELECTED && this.userId == userId

    fun canBeSelectedBy(userId: String): Boolean =
        status == SeatStatus.AVAILABLE ||
                (status == SeatStatus.SELECTED && this.userId == userId)
}