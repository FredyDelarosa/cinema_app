package com.fredy.cinema.presentation.seats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.cinema.core.session.UserSession
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.usecase.GetSeatsUseCase
import com.fredy.cinema.domain.usecase.ReleaseSeatUseCase
import com.fredy.cinema.domain.usecase.SelectSeatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeatsViewModel @Inject constructor(
    private val getSeatsUseCase: GetSeatsUseCase,
    private val selectSeatUseCase: SelectSeatUseCase,
    private val releaseSeatUseCase: ReleaseSeatUseCase,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])
    val userId: String = userSession.getUserId()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    val seats: StateFlow<List<Seat>> = getSeatsUseCase.observeSeats(roomId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshSeats()
    }

    fun refreshSeats() {
        viewModelScope.launch {
            getSeatsUseCase(roomId, forceRefresh = true)
        }
    }

    fun onSeatClick(seat: Seat) {
        viewModelScope.launch {
            if (seat.isSelectedBy(userId)) {
                releaseSeatUseCase(seat.id, userId, roomId).onFailure {
                    _error.emit(it.message ?: "Error releasing seat")
                }
            } else if (seat.isAvailable()) {
                selectSeatUseCase(seat.id, userId, roomId).onFailure {
                    _error.emit(it.message ?: "Error selecting seat")
                }
            }
        }
    }
}