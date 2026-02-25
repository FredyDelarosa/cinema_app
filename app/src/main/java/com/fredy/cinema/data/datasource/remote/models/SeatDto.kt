package com.fredy.cinema.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class SeatDto(
    @SerializedName("id") val id: String,
    @SerializedName("roomId") val roomId: String,
    @SerializedName("row") val row: String,
    @SerializedName("number") val number: Int,
    @SerializedName("status") val status: String,
    @SerializedName("userId") val userId: String?,
    @SerializedName("version") val version: Int
)

data class SeatsResponse(
    @SerializedName("seats") val seats: List<SeatDto>
)