package com.fredy.cinema.data.datasource.remote.websocket.models

import com.google.gson.annotations.SerializedName

enum class WebSocketMessageType {
    @SerializedName("SEAT_UPDATED")
    SEAT_UPDATED,

    @SerializedName("USER_JOINED")
    USER_JOINED,

    @SerializedName("USER_LEFT")
    USER_LEFT,

    @SerializedName("ROOM_SYNC")
    ROOM_SYNC,

    @SerializedName("ERROR")
    ERROR
}

data class WebSocketMessage(
    @SerializedName("type") val type: WebSocketMessageType,
    @SerializedName("roomId") val roomId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("payload") val payload: Map<String, Any>?,
    @SerializedName("sentAt") val sentAt: String
)

enum class ConnectionState {
    CONNECTING, CONNECTED, DISCONNECTED, ERROR
}