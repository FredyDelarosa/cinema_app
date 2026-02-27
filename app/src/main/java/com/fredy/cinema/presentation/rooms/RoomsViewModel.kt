package com.fredy.cinema.presentation.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.cinema.domain.model.Room
import com.fredy.cinema.domain.usecase.GetRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val getRoomsUseCase: GetRoomsUseCase
) : ViewModel() {

    val rooms: StateFlow<List<Room>> = getRoomsUseCase.observeRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshRooms()
    }

    fun refreshRooms() {
        viewModelScope.launch {
            getRoomsUseCase(forceRefresh = true)
        }
    }
}