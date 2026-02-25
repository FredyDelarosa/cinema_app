package com.fredy.cinema.domain.usecase

import com.fredy.cinema.domain.repository.CinemaRepository
import javax.inject.Inject

class ReleaseSeatUseCase @Inject constructor(
    private val repository: CinemaRepository
) {

    suspend operator fun invoke(seatId: String, userId: String, roomId: String): Result<Unit> {
        return repository.releaseSeat(seatId, userId, roomId)
    }
}