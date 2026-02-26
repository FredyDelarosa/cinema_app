package com.fredy.cinema.presentation.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.fredy.cinema.core.session.UserSession
import com.fredy.cinema.core.ui.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSession: UserSession,
    private val themeManager: ThemeManager
) : ViewModel() {
    val userId: String = userSession.getUserId()
    val isDarkTheme = themeManager.isDarkTheme
    
    fun toggleTheme(isDark: Boolean) {
        themeManager.setDarkTheme(isDark)
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkThemeOverride by viewModel.isDarkTheme.collectAsState()
    val isDarkTheme = isDarkThemeOverride ?: isSystemInDarkTheme()

    SettingsContent(
        userId = viewModel.userId,
        isDarkTheme = isDarkTheme,
        onBackClick = onBackClick,
        onThemeToggle = { viewModel.toggleTheme(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    userId: String,
    isDarkTheme: Boolean,
    onBackClick: () -> Unit,
    onThemeToggle: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ID del Usuario",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = userId,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isDarkTheme, 
                    onCheckedChange = onThemeToggle
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Cinema App v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MaterialTheme {
        SettingsContent(
            userId = "user_12345",
            isDarkTheme = false,
            onBackClick = {},
            onThemeToggle = {}
        )
    }
}
