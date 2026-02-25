package com.fredy.cinema.data.datasource.remote.api

import com.fredy.cinema.data.datasource.remote.models.*
import retrofit2.http.*

interface CinemaApi {

    @GET("rooms")
    suspend fun getRooms(): RoomsResponse

    @GET("rooms/{roomId}")
    suspend fun getRoomById(@Path("roomId") roomId: String): RoomDto

    @GET("rooms/{roomId}/seats")
    suspend fun getSeatsByRoom(@Path("roomId") roomId: String): SeatsResponse

    @POST("seats/select")
    suspend fun selectSeat(@Body request: SelectSeatRequest): SelectSeatResponse

    @POST("seats/release")
    suspend fun releaseSeat(@Body request: SelectSeatRequest): SelectSeatResponse
}