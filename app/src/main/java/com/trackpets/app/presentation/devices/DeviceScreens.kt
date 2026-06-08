package com.trackpets.app.presentation.devices

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
import com.trackpets.app.domain.model.Device
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun DeviceListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    TrackPetsScaffold(
        title = "Dispositivos",
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) }, modifier = Modifier.padding(16.dp))

            when (listState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView((listState as UiState.Error).message, { viewModel.loadItems(refresh = true) })
                is UiState.Empty -> EmptyStateView("No hay dispositivos", "Añade un nuevo dispositivo usando el botón +")
                is UiState.Success -> {
                    val data = (listState as UiState.Success).data
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(data.results, key = { it.id }) { device ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onNavigateToDetail(device.id) },
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
                                            Icon(Icons.Default.Router, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("IMEI: ${device.imei}", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            StatusBadge(
                                                text = if (device.isActive) "Activo" else "Inactivo",
                                                backgroundColor = if (device.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                                textColor = if (device.isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Modelo: ${device.model}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun DeviceDetailScreen(id: Int, onNavigateBack: () -> Unit, onNavigateToEdit: (Int) -> Unit, onDeleted: () -> Unit = onNavigateBack, viewModel: DeviceViewModel = hiltViewModel()) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(id) { viewModel.loadDevice(id) }

    TrackPetsScaffold(
        title = "Detalle de Dispositivo",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
        actions = {
            IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
        }
    ) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadDevice(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val device = (detailState as UiState.Success).data
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
                                    Icon(Icons.Default.Router, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("IMEI: ${device.imei}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            StatusBadge(
                                text = if (device.isActive) "Activo" else "Inactivo",
                                backgroundColor = if (device.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                textColor = if (device.isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                            
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))

                            DetailRow(icon = Icons.Default.Devices, label = "Modelo", value = device.model)
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Map, label = "Ubicación", value = "${device.latitude}, ${device.longitude}")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.DateRange, label = "Instalación", value = device.installationDate)
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.EventRepeat, label = "Renovación", value = device.renewalDate)
                        }
                    }
                }
                if (showDeleteDialog) {
                    ConfirmDeleteDialog(itemName = device.imei, onConfirm = { viewModel.deleteDevice(id); onDeleted() }, onDismiss = { showDeleteDialog = false })
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceFormScreen(id: Int?, onNavigateBack: () -> Unit, onSaved: () -> Unit = {}, viewModel: DeviceViewModel = hiltViewModel()) {
    var ownerId by remember { mutableStateOf("") }
    var imei by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var latitude by remember { mutableStateOf("0.0") }
    var longitude by remember { mutableStateOf("0.0") }
    var model by remember { mutableStateOf("") }
    var instDate by remember { mutableStateOf("2024-01-01") }
    var renDate by remember { mutableStateOf("2025-01-01") }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) { if (id != null) viewModel.loadDevice(id) }
    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val d = (detailState as UiState.Success).data
            ownerId = d.ownerId.toString(); imei = d.imei; isActive = d.isActive; latitude = d.latitude.toString()
            longitude = d.longitude.toString(); model = d.model; instDate = d.installationDate; renDate = d.renewalDate
        }
    }
    LaunchedEffect(formState) { if (formState is UiState.Success) { viewModel.resetFormState(); onSaved(); onNavigateBack() } }

    TrackPetsScaffold(
        title = if (id == null) "Nuevo Dispositivo" else "Editar Dispositivo",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = ownerId, 
                onValueChange = { ownerId = it }, 
                label = { Text("ID del Dueño") }, 
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = imei, 
                onValueChange = { imei = it }, 
                label = { Text("IMEI") }, 
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = model, 
                onValueChange = { model = it }, 
                label = { Text("Modelo") }, 
                leadingIcon = { Icon(Icons.Default.Devices, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { 
                Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                Text("Dispositivo Activo") 
            }
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
            Row { 
                OutlinedTextField(
                    value = instDate, 
                    onValueChange = { instDate = it }, 
                    label = { Text("F. Instalación") }, 
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = renDate, 
                    onValueChange = { renDate = it }, 
                    label = { Text("F. Renovación") }, 
                    leadingIcon = { Icon(Icons.Default.EventRepeat, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                ) 
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.saveDevice(Device(id = id ?: 0, ownerId = ownerId.toIntOrNull() ?: 0, imei = imei, isActive = isActive, latitude = latitude.toDoubleOrNull() ?: 0.0, longitude = longitude.toDoubleOrNull() ?: 0.0, model = model, installationDate = instDate, renewalDate = renDate)) },
                modifier = Modifier.fillMaxWidth(), enabled = formState !is UiState.Loading && (ownerId.toIntOrNull() ?: 0) > 0
            ) { if (formState is UiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp)) else Text("Guardar") }
            if (formState is UiState.Error) { Text((formState as UiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}
