package com.fredy.cinema.presentation.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.cinema.data.datasource.remote.api.CinemaApi
import com.fredy.cinema.data.datasource.remote.models.CreateRoomRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoomViewModel @Inject constructor(
    private val api: CinemaApi
) : ViewModel() {
    fun createRoom(movie: String, time: String, price: Double, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.createRoom(
                    CreateRoomRequest(
                        movie = movie,
                        posterUrl = "https://via.placeholder.com/150",
                        time = time,
                        price = price,
                        rows = 8,
                        seatsPerRow = 5
                    )
                )
                onCreated(response.room.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    onRoomCreated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateRoomViewModel = hiltViewModel()
) {
    var movie by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Room") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = movie, onValueChange = { movie = it }, label = { Text("Movie Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 20:00)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = { viewModel.createRoom(movie, time, price.toDoubleOrNull() ?: 0.0, onRoomCreated) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Room and Go to Seats")
            }
        }
    }
}