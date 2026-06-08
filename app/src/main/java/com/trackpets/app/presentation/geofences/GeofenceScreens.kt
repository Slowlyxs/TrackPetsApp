package com.trackpets.app.presentation.geofences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.domain.model.Geofence
import com.trackpets.app.domain.model.Pet
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.pets.PetViewModel
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun GeofenceListScreen(onNavigateToDetail: (Int) -> Unit, onNavigateToCreate: () -> Unit, viewModel: GeofenceViewModel = hiltViewModel()) {
    val listState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    TrackPetsScaffold(title = "Geocercas", floatingActionButton = { FloatingActionButton(onClick = onNavigateToCreate, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Añadir") } }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) }, modifier = Modifier.padding(16.dp))
            when (listState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView((listState as UiState.Error).message, { viewModel.loadItems(refresh = true) })
                is UiState.Empty -> EmptyStateView("No hay geocercas", "Añade una nueva geocerca usando el botón +")
                is UiState.Success -> {
                    val data = (listState as UiState.Success).data
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(data.results, key = { it.id }) { geofence ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onNavigateToDetail(geofence.id) },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ShareLocation, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(geofence.name, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${geofence.latitude}, ${geofence.longitude}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.SocialDistance, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Radio: ${geofence.radius}m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            if (data.next != null) TextButton(onClick = { viewModel.loadMore() }, modifier = Modifier.fillMaxWidth()) { Text("Cargar más...") }
                            else PaginationFooter(isLoading = false, hasMore = false)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceDetailScreen(id: Int, onNavigateBack: () -> Unit, onNavigateToEdit: (Int) -> Unit, onDeleted: () -> Unit = onNavigateBack, viewModel: GeofenceViewModel = hiltViewModel()) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(id) { viewModel.loadGeofence(id) }
    TrackPetsScaffold(title = "Detalle de Geocerca", navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, actions = { IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }; IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") } }) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadGeofence(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val g = (detailState as UiState.Success).data
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ShareLocation, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(g.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                            
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))

                            DetailRow(icon = Icons.Default.Pets, label = "Mascota Asignada (ID)", value = "${g.petId}")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.SocialDistance, label = "Radio", value = "${g.radius}m")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Map, label = "Coordenadas (Lat, Lng)", value = "${g.latitude}, ${g.longitude}")
                        }
                    }
                }
                if (showDeleteDialog) { ConfirmDeleteDialog(itemName = g.name, onConfirm = { viewModel.deleteGeofence(id); onDeleted() }, onDismiss = { showDeleteDialog = false }) }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceFormScreen(
    id: Int?,
    initialPetId: Int? = null,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: GeofenceViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var name by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("0.0") }
    var longitude by remember { mutableStateOf("0.0") }
    var radius by remember { mutableStateOf("100.0") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val petsState by petViewModel.listState.collectAsState()
    val availablePets = (petsState as? UiState.Success)?.data?.results ?: emptyList()

    LaunchedEffect(Unit) { 
        if (petsState !is UiState.Success) {
            petViewModel.loadItems(refresh = true) 
        }
    }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) { if (id != null) viewModel.loadGeofence(id) }
    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val g = (detailState as UiState.Success).data
            name = g.name; latitude = g.latitude.toString()
            longitude = g.longitude.toString(); radius = g.radius.toString()
        }
    }
    LaunchedEffect(initialPetId, availablePets) {
        if (initialPetId != null && selectedPet == null) {
            selectedPet = availablePets.find { it.id == initialPetId }
        }
    }
    LaunchedEffect(formState) {
        if (formState is UiState.Success) { viewModel.resetFormState(); onSaved(); onNavigateBack() }
    }

    val petIdInt = selectedPet?.id ?: 0
    val canSave = formState !is UiState.Loading && petIdInt > 0 && name.isNotBlank()

    TrackPetsScaffold(
        title = if (id == null) "Nueva Geocerca" else "Editar Geocerca",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // ── Selector de Mascota real desde la API ──────────────
            Text("Mascota *", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedPet?.let { "[${it.id}] ${it.name}" } ?: "Selecciona una mascota",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mascota") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    isError = petIdInt <= 0
                )
                ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                    if (availablePets.isEmpty()) {
                        DropdownMenuItem(text = { Text("Cargando mascotas...") }, onClick = {})
                    } else {
                        availablePets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text("[${pet.id}] ${pet.name} (${pet.species})") },
                                onClick = { selectedPet = pet; dropdownExpanded = false }
                            )
                        }
                    }
                }
            }
            if (petIdInt <= 0) Text("Debes seleccionar una mascota", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("Nombre de la Geocerca *") }, 
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(), 
                isError = name.isBlank()
            )
            if (name.isBlank()) Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(16.dp))
            Row {
                OutlinedTextField(
                    value = latitude, 
                    onValueChange = { latitude = it }, 
                    label = { Text("Latitud") }, 
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = longitude, 
                    onValueChange = { longitude = it }, 
                    label = { Text("Longitud") }, 
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = radius, 
                onValueChange = { radius = it }, 
                label = { Text("Radio (metros)") }, 
                leadingIcon = { Icon(Icons.Default.SocialDistance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.saveGeofence(Geofence(
                        id = id ?: 0, petId = petIdInt, name = name,
                        latitude = latitude.toDoubleOrNull() ?: 0.0,
                        longitude = longitude.toDoubleOrNull() ?: 0.0,
                        radius = radius.toDoubleOrNull() ?: 100.0
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave
            ) {
                if (formState is UiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Guardar")
            }
            if (formState is UiState.Error) {
                Text((formState as UiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
