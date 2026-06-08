package com.trackpets.app.presentation.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.presentation.components.TrackPetsScaffold
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun MoreScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToGeofences: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val isStaff by viewModel.isStaff.collectAsState()

    LaunchedEffect(logoutState) {
        if (logoutState is UiState.Success) {
            onNavigateToLogin()
        }
    }

    TrackPetsScaffold(
        title = "Más Opciones"
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (isStaff) {
                MoreMenuItem(
                    icon = Icons.Default.Person,
                    title = "Usuarios (Admin)",
                    onClick = onNavigateToUsers
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            MoreMenuItem(
                icon = Icons.Default.Map,
                title = "Geocercas",
                onClick = onNavigateToGeofences
            )
            Spacer(modifier = Modifier.height(8.dp))
            MoreMenuItem(
                icon = Icons.Default.Notifications,
                title = "Alertas",
                onClick = onNavigateToAlerts
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = logoutState !is UiState.Loading
            ) {
                if (logoutState is UiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onError, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }

            if (logoutState is UiState.Error) {
                Text(
                    text = (logoutState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
