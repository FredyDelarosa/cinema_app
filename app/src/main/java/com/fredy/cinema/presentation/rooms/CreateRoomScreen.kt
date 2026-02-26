package com.fredy.cinema.presentation.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    fun createRoom(
        movie: String,
        posterUrl: String,
        time: String,
        price: Double,
        rows: Int,
        seatsPerRow: Int,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.createRoom(
                    CreateRoomRequest(
                        movie = movie,
                        posterUrl = posterUrl,
                        time = time,
                        price = price,
                        rows = rows,
                        seatsPerRow = seatsPerRow
                    )
                )
                onCreated(response.room.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun CreateRoomScreen(
    onRoomCreated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateRoomViewModel = hiltViewModel()
) {
    CreateRoomContent(
        onBack = onBack,
        onCreateRoom = { movie, poster, time, price, rows, seats ->
            viewModel.createRoom(movie, poster, time, price, rows, seats, onRoomCreated)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomContent(
    onBack: () -> Unit,
    onCreateRoom: (String, String, String, Double, Int, Int) -> Unit
) {
    var movie by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf("7") }
    var seatsPerRow by remember { mutableStateOf("10") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Sala") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = movie,
                onValueChange = { movie = it },
                label = { Text("Nombre de la Película") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Inside Out 3") }
            )
            
            OutlinedTextField(
                value = posterUrl,
                onValueChange = { posterUrl = it },
                label = { Text("URL del Póster") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://image.tmdb.org/t/p/...") }
            )
            
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Horario") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2025-12-24 16:30") }
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("80.00") }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = rows,
                    onValueChange = { rows = it },
                    label = { Text("Filas") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = seatsPerRow,
                    onValueChange = { seatsPerRow = it },
                    label = { Text("Asientos/Fila") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Button(
                onClick = { 
                    onCreateRoom(
                        movie, 
                        posterUrl, 
                        time, 
                        price.toDoubleOrNull() ?: 0.0,
                        rows.toIntOrNull() ?: 7,
                        seatsPerRow.toIntOrNull() ?: 10
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = movie.isNotBlank() && time.isNotBlank()
            ) {
                Text("Crear Sala y Configurar Asientos")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRoomPreview() {
    MaterialTheme {
        CreateRoomContent(onBack = {}, onCreateRoom = { _, _, _, _, _, _ -> })
    }
}
