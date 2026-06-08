package com.trackpets.app.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.presentation.alerts.AlertViewModel
import com.trackpets.app.presentation.devices.DeviceViewModel
import com.trackpets.app.presentation.geofences.GeofenceViewModel
import com.trackpets.app.presentation.pets.PetViewModel
import com.trackpets.app.presentation.uiState.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToPets: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToGeofences: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    deviceViewModel: DeviceViewModel = hiltViewModel(),
    geofenceViewModel: GeofenceViewModel = hiltViewModel(),
    alertViewModel: AlertViewModel = hiltViewModel()
) {
    val petState by petViewModel.listState.collectAsState()
    val deviceState by deviceViewModel.listState.collectAsState()
    val geofenceState by geofenceViewModel.listState.collectAsState()
    val alertState by alertViewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        if (petState !is UiState.Success) petViewModel.loadItems()
        if (deviceState !is UiState.Success) deviceViewModel.loadItems()
        if (geofenceState !is UiState.Success) geofenceViewModel.loadItems()
        if (alertState !is UiState.Success) alertViewModel.loadItems()
    }

    val petCount = if (petState is UiState.Success) (petState as UiState.Success).data.count else 0
    val deviceCount = if (deviceState is UiState.Success) (deviceState as UiState.Success).data.count else 0
    val geofenceCount = if (geofenceState is UiState.Success) (geofenceState as UiState.Success).data.count else 0
    val alertCount = if (alertState is UiState.Success) (alertState as UiState.Success).data.count else 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TrackPets Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen General",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DashboardCard(
                        title = "Mascotas",
                        count = petCount,
                        icon = Icons.Default.Pets,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = onNavigateToPets
                    )
                }
                item {
                    DashboardCard(
                        title = "Dispositivos",
                        count = deviceCount,
                        icon = Icons.Default.DeviceUnknown,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = onNavigateToDevices
                    )
                }
                item {
                    DashboardCard(
                        title = "Geocercas",
                        count = geofenceCount,
                        icon = Icons.Default.Map,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = onNavigateToGeofences
                    )
                }
                item {
                    DashboardCard(
                        title = "Alertas",
                        count = alertCount,
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.errorContainer,
                        onClick = onNavigateToAlerts
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onNavigateToPets,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Mascota")
                }
                Button(
                    onClick = onNavigateToGeofences,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Geocerca")
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
