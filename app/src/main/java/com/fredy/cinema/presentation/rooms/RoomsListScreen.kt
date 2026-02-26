package com.fredy.cinema.presentation.rooms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fredy.cinema.domain.model.Room

@Composable
fun RoomsListScreen(
    onRoomClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    viewModel: RoomsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RoomsListContent(
        uiState = uiState,
        onRoomClick = onRoomClick,
        onSettingsClick = onSettingsClick,
        onCreateRoomClick = onCreateRoomClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsListContent(
    uiState: RoomsUiState,
    onRoomClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateRoomClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cartelera de Cine") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRoomClick) {
                Icon(Icons.Default.Add, contentDescription = "Crear Sala")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error?.let { error ->
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.rooms) { room ->
                    MovieCard(room = room, onClick = { onRoomClick(room.id) })
                }
            }
        }
    }
}

@Composable
fun MovieCard(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(150.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(room.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = room.movie,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = room.movie,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Horario: ${room.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "$${room.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomsListPreview() {
    MaterialTheme {
        RoomsListContent(
            uiState = RoomsUiState(
                rooms = listOf(
                    Room("1", "Inception", "https://image.tmdb.org/t/p/w500/9gk7Fn9sVAsOX7v9M9Y3ST3UUA2.jpg", "20:00", 12.5, ""),
                    Room("2", "Interstellar", "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", "22:30", 15.0, "")
                )
            ),
            onRoomClick = {},
            onSettingsClick = {},
            onCreateRoomClick = {}
        )
    }
}
