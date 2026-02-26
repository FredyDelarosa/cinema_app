package com.fredy.cinema.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class CreateRoomRequest(
    @SerializedName("movie") val movie: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("time") val time: String,
    @SerializedName("price") val price: Double,
    @SerializedName("rows") val rows: Int,
    @SerializedName("seatsPerRow") val seatsPerRow: Int
)

data class CreateRoomResponse(
    @SerializedName("room") val room: RoomDto
)