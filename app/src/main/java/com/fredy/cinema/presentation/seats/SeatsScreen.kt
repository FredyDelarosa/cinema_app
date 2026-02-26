package com.fredy.cinema.presentation.seats

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredy.cinema.domain.model.Seat
import com.fredy.cinema.domain.model.SeatStatus
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatsScreen(
    onBackClick: () -> Unit,
    viewModel: SeatsViewModel = hiltViewModel()
) {
    val seats by viewModel.seats.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Select Your Seats", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Screen Visual Indicator
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            ) {}
            Text(
                text = "SCREEN",
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
                    SeatItem(
                        seat = seat,
                        currentUserId = viewModel.userId,
                        onClick = { viewModel.onSeatClick(seat) }
                    )
                }
            }

            // Legend Section
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
                    LegendItem("Available", Color.LightGray)
                    LegendItem("Selected", MaterialTheme.colorScheme.primary)
                    LegendItem("Booked", MaterialTheme.colorScheme.error)
                }
            }
            
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm Selection", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SeatItem(seat: Seat, currentUserId: String, onClick: () -> Unit) {
    val isSelectedByMe = seat.isSelectedBy(currentUserId)
    val isBookedByOthers = seat.status == SeatStatus.BOOKED || (seat.status == SeatStatus.SELECTED && !isSelectedByMe)
    
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
            .background(containerColor, RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = if (seat.isAvailable()) MaterialTheme.colorScheme.outline else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = seat.isAvailable() || isSelectedByMe,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.getDisplayName(),
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
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