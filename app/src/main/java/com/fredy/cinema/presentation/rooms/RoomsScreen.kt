package com.fredy.cinema.presentation.rooms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fredy.cinema.domain.model.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onRoomClick: (String) -> Unit,
    onCreateRoomClick: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    val rooms by viewModel.rooms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cinema Rooms") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRoomClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Room")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rooms) { room ->
                RoomItem(room = room, onClick = { onRoomClick(room.id) })
            }
        }
    }
}

@Composable
fun RoomItem(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(120.dp)
        ) {
            AsyncImage(
                model = room.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = room.movie, style = MaterialTheme.typography.titleLarge)
                Text(text = "Time: ${room.time}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: $${room.price}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}