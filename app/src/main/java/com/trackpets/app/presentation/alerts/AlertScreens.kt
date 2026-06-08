package com.trackpets.app.presentation.alerts

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.domain.model.Alert
import com.trackpets.app.domain.model.Pet
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.pets.PetViewModel
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun AlertListScreen(onNavigateToDetail: (Int) -> Unit, onNavigateToCreate: () -> Unit, viewModel: AlertViewModel = hiltViewModel()) {
    val listState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    TrackPetsScaffold(title = "Alertas", floatingActionButton = { FloatingActionButton(onClick = onNavigateToCreate, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, contentDescription = "Añadir") } }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) }, modifier = Modifier.padding(16.dp))
            when (listState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView((listState as UiState.Error).message, { viewModel.loadItems(refresh = true) })
                is UiState.Empty -> EmptyStateView("No hay alertas", "Añade una nueva alerta usando el botón +")
                is UiState.Success -> {
                    val data = (listState as UiState.Success).data
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(data.results, key = { it.id }) { alert ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onNavigateToDetail(alert.id) },
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
                                        color = if (alert.isActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = if (alert.isActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(alert.type.uppercase(), style = MaterialTheme.typography.titleMedium, color = if (alert.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(alert.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(alert.createdAt ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun AlertDetailScreen(id: Int, onNavigateBack: () -> Unit, onNavigateToEdit: (Int) -> Unit, onDeleted: () -> Unit = onNavigateBack, viewModel: AlertViewModel = hiltViewModel()) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(id) { viewModel.loadAlert(id) }
    TrackPetsScaffold(title = "Detalle de Alerta", navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, actions = { IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }; IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") } }) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadAlert(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val a = (detailState as UiState.Success).data
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = if (a.isActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = if (a.isActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(a.type.uppercase(), style = MaterialTheme.typography.headlineSmall, color = if (a.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                            
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))

                            DetailRow(icon = Icons.Default.Pets, label = "Mascota Asignada (ID)", value = "${a.petId}")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Description, label = "Descripción", value = a.description)
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Info, label = "Estado", value = if (a.isActive) "Activa" else "Inactiva")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Schedule, label = "Fecha de Creación", value = a.createdAt ?: "Desconocida")
                        }
                    }
                }
                if (showDeleteDialog) { ConfirmDeleteDialog(itemName = "Alerta ${a.id}", onConfirm = { viewModel.deleteAlert(id); onDeleted() }, onDismiss = { showDeleteDialog = false }) }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertFormScreen(
    id: Int?,
    initialPetId: Int? = null,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: AlertViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var type by remember { mutableStateOf("salida_geocerca") }
    var description by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var dropdownPetExpanded by remember { mutableStateOf(false) }
    var dropdownTypeExpanded by remember { mutableStateOf(false) }

    val tiposAlert = listOf("perdida", "encontrada", "salida_geocerca")

    val petsState by petViewModel.listState.collectAsState()
    val availablePets = (petsState as? UiState.Success)?.data?.results ?: emptyList()

    LaunchedEffect(Unit) { 
        if (petsState !is UiState.Success) {
            petViewModel.loadItems(refresh = true) 
        }
    }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) { if (id != null) viewModel.loadAlert(id) }
    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val a = (detailState as UiState.Success).data
            type = a.type; description = a.description; isActive = a.isActive
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
    val canSave = formState !is UiState.Loading && petIdInt > 0

    TrackPetsScaffold(
        title = if (id == null) "Nueva Alerta" else "Editar Alerta",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // ── Selector de Mascota real desde la API ──────────────
            Text("Mascota *", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(expanded = dropdownPetExpanded, onExpandedChange = { dropdownPetExpanded = it }) {
                OutlinedTextField(
                    value = selectedPet?.let { "[${it.id}] ${it.name}" } ?: "Selecciona una mascota",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mascota") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownPetExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    isError = petIdInt <= 0
                )
                ExposedDropdownMenu(expanded = dropdownPetExpanded, onDismissRequest = { dropdownPetExpanded = false }) {
                    if (availablePets.isEmpty()) {
                        DropdownMenuItem(text = { Text("Cargando mascotas...") }, onClick = {})
                    } else {
                        availablePets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text("[${pet.id}] ${pet.name} (${pet.species})") },
                                onClick = { selectedPet = pet; dropdownPetExpanded = false }
                            )
                        }
                    }
                }
            }
            if (petIdInt <= 0) Text("Debes seleccionar una mascota", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))

            // ── Selector de Tipo (enum válido para Django) ─────────
            ExposedDropdownMenuBox(expanded = dropdownTypeExpanded, onExpandedChange = { dropdownTypeExpanded = it }) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de alerta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTypeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(expanded = dropdownTypeExpanded, onDismissRequest = { dropdownTypeExpanded = false }) {
                    tiposAlert.forEach { t ->
                        DropdownMenuItem(text = { Text(t) }, onClick = { type = t; dropdownTypeExpanded = false })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = description, 
                onValueChange = { description = it }, 
                label = { Text("Descripción") }, 
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                Text("¿Alerta Activa?")
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.saveAlert(Alert(
                        id = id ?: 0,
                        petId = petIdInt,
                        type = type,
                        description = description,
                        isActive = isActive,
                        createdAt = null
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
