package com.fredy.cinema.domain.usecase

import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.repository.CinemaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoomsUseCase @Inject constructor(
    private val repository: CinemaRepository
) {

    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Room>> {
        return repository.getRooms(forceRefresh)
    }

    fun observeRooms(): Flow<List<Room>> = repository.observeRooms()
}