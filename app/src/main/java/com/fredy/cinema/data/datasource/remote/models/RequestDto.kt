package com.fredy.cinema.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class SelectSeatRequest(
    @SerializedName("seatId") val seatId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("roomId") val roomId: String
)

data class SelectSeatResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)