package com.fredy.cinema.domain.usecase

import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.repository.CinemaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeatsUseCase @Inject constructor(
    private val repository: CinemaRepository
) {

    suspend operator fun invoke(roomId: String, forceRefresh: Boolean = false): Result<List<Seat>> {
        return repository.getSeats(roomId, forceRefresh)
    }

    fun observeSeats(roomId: String): Flow<List<Seat>> = repository.observeSeats(roomId)
}