package com.fredy.cinema.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class RoomDto(
    @SerializedName("id") val id: String,
    @SerializedName("movie") val movie: String,
    @SerializedName("posterUrl") val posterUrl: String?,
    @SerializedName("time") val time: String,
    @SerializedName("price") val price: Double,
    @SerializedName("createdAt") val createdAt: String
)

data class RoomsResponse(
    @SerializedName("rooms") val rooms: List<RoomDto>
)