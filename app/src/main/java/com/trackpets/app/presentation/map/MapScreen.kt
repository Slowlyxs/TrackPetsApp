package com.trackpets.app.presentation.map

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.trackpets.app.domain.model.Pet
import com.trackpets.app.presentation.components.TrackPetsScaffold
import com.trackpets.app.presentation.geofences.GeofenceViewModel
import com.trackpets.app.presentation.pets.PetViewModel
import com.trackpets.app.presentation.uiState.UiState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

data class TrackedPet(
    val id: Int,
    val name: String,
    val species: String,
    val isConnected: Boolean,
    val latitude: Double,
    val longitude: Double,
    val lastUpdate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToGeofence: (Int?) -> Unit = {},
    petViewModel: PetViewModel = hiltViewModel(),
    geofenceViewModel: GeofenceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val listState by petViewModel.listState.collectAsState()
    val geofenceListState by geofenceViewModel.listState.collectAsState()
    
    // Initialize osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        if (listState !is UiState.Success) {
            petViewModel.loadItems()
        }
        if (geofenceListState !is UiState.Success) {
            geofenceViewModel.loadItems()
        }
    }

    val pets: List<TrackedPet> = remember(listState) {
        if (listState is UiState.Success<*>) {
            val realPets = (listState as UiState.Success<com.trackpets.app.data.dto.PaginatedResponse<Pet>>).data.results
            realPets.mapIndexed { index, pet ->
                // Mock coordinates around Quito center for demonstration (-0.2298, -78.5249)
                val latOffset = (index * 0.01) * (if (index % 2 == 0) 1 else -1)
                val lngOffset = (index * 0.01) * (if (index % 3 == 0) 1 else -1)
                TrackedPet(
                    id = pet.id ?: 0,
                    name = pet.name,
                    species = "${pet.species} - ${pet.breed}",
                    isConnected = true,
                    latitude = -0.2298 + latOffset,
                    longitude = -78.5249 + lngOffset,
                    lastUpdate = "Sincronizado"
                )
            }
        } else {
            emptyList()
        }
    }

    val geofences = remember(geofenceListState) {
        if (geofenceListState is UiState.Success<*>) {
            (geofenceListState as UiState.Success<com.trackpets.app.data.dto.PaginatedResponse<com.trackpets.app.domain.model.Geofence>>).data.results
        } else {
            emptyList()
        }
    }

    // Lifecycle aware MapView
    val mapView = remember { MapView(context) }

    var selectedPetLocation by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(selectedPetLocation) {
        selectedPetLocation?.let { loc ->
            mapView.controller.animateTo(loc)
            mapView.controller.setZoom(16.0)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    TrackPetsScaffold(
        title = "Mapa de Mascotas",
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToGeofence(null) }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.AddLocation, contentDescription = "Crear Geocerca")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Map takes 60% of screen
            Box(modifier = Modifier.weight(0.6f).fillMaxWidth()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { 
                        mapView.apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(13.0)
                            val quitoPoint = GeoPoint(-0.2298, -78.5249)
                            controller.setCenter(quitoPoint)
                        }
                    },
                    update = { map ->
                        // Remove previous custom overlays but keep system ones
                        map.overlays.removeAll { it is Marker || it is Polygon }

                        // Draw Geofences
                        geofences.forEach { geo ->
                            val geoPoint = GeoPoint(geo.latitude, geo.longitude)
                            
                            val polygon = Polygon(map)
                            val circlePoints = Polygon.pointsAsCircle(geoPoint, geo.radius)
                            polygon.points = circlePoints
                            polygon.fillPaint.color = android.graphics.Color.argb(50, 0, 255, 0) // Semi-transparent green
                            polygon.outlinePaint.color = android.graphics.Color.GREEN
                            polygon.outlinePaint.strokeWidth = 3f
                            polygon.title = "Geocerca: ${geo.name}"
                            map.overlays.add(polygon)
                            
                            val markerGeo = Marker(map)
                            markerGeo.position = geoPoint
                            markerGeo.title = "Geocerca: ${geo.name}"
                            markerGeo.snippet = "Radio: ${geo.radius}m"
                            markerGeo.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            // Utilizar un ícono pequeño o texto si es posible, o dejar el default
                            map.overlays.add(markerGeo)
                        }

                        // Draw Pets
                        pets.forEach { pet ->
                            val marker = Marker(map)
                            marker.position = GeoPoint(pet.latitude, pet.longitude)
                            marker.title = "${pet.name} (Ver Geocercas)"
                            marker.snippet = "Presiona para editar geocercas"
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            marker.setOnMarkerClickListener { m, _ ->
                                m.showInfoWindow()
                                onNavigateToGeofence(pet.id)
                                true
                            }
                            map.overlays.add(marker)
                        }
                        map.invalidate()
                    }
                )
            }

            // Bottom panel with pet list takes 40%
            Surface(
                modifier = Modifier.weight(0.4f).fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Mascotas Rastreadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyColumn {
                        items(pets, key = { it.id }) { pet ->
                            PetTrackingCard(pet = pet, onClick = {
                                selectedPetLocation = GeoPoint(pet.latitude, pet.longitude)
                            })
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetTrackingCard(pet: TrackedPet, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pet icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (pet.isConnected) Color(0xFF0C5A35).copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = if (pet.isConnected) Color(0xFF0C5A35)
                        else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    pet.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    pet.species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (pet.isConnected) Color(0xFF0C5A35) else MaterialTheme.colorScheme.error)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (pet.isConnected) "Conectado" else "Desconectado",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pet.isConnected) Color(0xFF0C5A35) else MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    "Última act: ${pet.lastUpdate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
