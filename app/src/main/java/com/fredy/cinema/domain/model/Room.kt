package com.fredy.cinema.domain.model

data class Room(
    val id: String,
    val movie: String,
    val posterUrl: String?,
    val time: String,
    val price: Double,
    val createdAt: String
)