package com.fredy.cinema.presentation.seats

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.model.SeatStatus
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SeatsScreen(
    onBackClick: () -> Unit,
    viewModel: SeatsViewModel = hiltViewModel()
) {
    val seats by viewModel.seats.collectAsState()
    val loadingSeats by viewModel.loadingSeats.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    SeatsContent(
        seats = seats,
        loadingSeats = loadingSeats,
        userId = viewModel.userId,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onSeatClick = { viewModel.onSeatClick(it) },
        onConfirmClick = {
            // Se queda en la misma pantalla por petición del usuario
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatsContent(
    seats: List<Seat>,
    loadingSeats: Set<String>,
    userId: String,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSeatClick: (Seat) -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Selecciona tus asientos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            ) {}
            Text(
                text = "PANTALLA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(seats) { seat ->
                    val isLoading = loadingSeats.contains(seat.id)
                    SeatItem(
                        seat = seat,
                        currentUserId = userId,
                        isLoading = isLoading,
                        onClick = { onSeatClick(seat) }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem("Disponible", Color.LightGray)
                    LegendItem("Seleccionado", MaterialTheme.colorScheme.primary)
                    LegendItem("Ocupado", MaterialTheme.colorScheme.error)
                }
            }
            
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirmar selección", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SeatItem(
    seat: Seat, 
    currentUserId: String, 
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val isSelectedByMe = seat.isSelectedBy(currentUserId)
    val isBookedByOthers = seat.status == SeatStatus.BOOKED || (seat.status == SeatStatus.SELECTED && !isSelectedByMe)
    
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val containerColor = when {
        isBookedByOthers -> MaterialTheme.colorScheme.error
        isSelectedByMe -> MaterialTheme.colorScheme.primary
        else -> Color.LightGray.copy(alpha = 0.5f)
    }
    
    val contentColor = when {
        isBookedByOthers || isSelectedByMe -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .alpha(if (isLoading) alpha else 1f)
            .background(containerColor, RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = if (seat.isAvailable()) MaterialTheme.colorScheme.outline else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = (seat.isAvailable() || isSelectedByMe) && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            Text(
                text = seat.getDisplayName(),
                color = contentColor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun SeatsPreview() {
    MaterialTheme {
        SeatsContent(
            seats = listOf(
                Seat("1", "room1", "A", 1, SeatStatus.AVAILABLE, null, 1),
                Seat("2", "room1", "A", 2, SeatStatus.SELECTED, "me", 1),
                Seat("3", "room1", "A", 3, SeatStatus.BOOKED, "other", 1),
                Seat("4", "room1", "A", 4, SeatStatus.AVAILABLE, null, 1),
                Seat("5", "room1", "A", 5, SeatStatus.AVAILABLE, null, 1)
            ),
            loadingSeats = setOf("4"),
            userId = "me",
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onSeatClick = {},
            onConfirmClick = {}
        )
    }
}
