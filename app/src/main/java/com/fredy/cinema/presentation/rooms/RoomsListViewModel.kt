package com.fredy.cinema.presentation.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.usecase.GetRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomsUiState(
    val isLoading: Boolean = false,
    val rooms: List<Room> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class RoomsListViewModel @Inject constructor(
    private val getRoomsUseCase: GetRoomsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomsUiState())
    val uiState: StateFlow<RoomsUiState> = _uiState.asStateFlow()

    init {
        observeRooms()
        refreshRooms()
    }

    private fun observeRooms() {
        getRoomsUseCase.observeRooms()
            .onEach { rooms ->
                _uiState.update { it.copy(rooms = rooms, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun refreshRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getRoomsUseCase(forceRefresh = true).onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}