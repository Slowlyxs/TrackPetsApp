package com.trackpets.app.presentation.pets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.domain.model.Pet
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun PetListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    TrackPetsScaffold(
        title = "Mascotas",
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.onSearchQueryChanged(it)
                },
                modifier = Modifier.padding(16.dp)
            )

            when (listState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorView((listState as UiState.Error).message, { viewModel.loadItems(refresh = true) })
                is UiState.Empty -> EmptyStateView("No hay mascotas", "Añade una nueva mascota usando el botón +")
                is UiState.Success -> {
                    val data = (listState as UiState.Success).data
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(data.results, key = { it.id }) { pet ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onNavigateToDetail(pet.id) },
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
                                            Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pet.name, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            StatusBadge(
                                                text = pet.species,
                                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Raza: ${pet.breed}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            if (data.next != null) {
                                TextButton(
                                    onClick = { viewModel.loadMore() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cargar más...")
                                }
                            } else {
                                PaginationFooter(isLoading = false, hasMore = false)
                            }
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
fun PetDetailScreen(
    id: Int,
    refreshKey: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onDeleted: () -> Unit = onNavigateBack,
    viewModel: PetViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id, refreshKey) {
        viewModel.loadPet(id)
    }

    TrackPetsScaffold(
        title = "Detalle de Mascota",
        navigationIcon = {
            IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
        },
        actions = {
            IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
        }
    ) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadPet(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val pet = (detailState as UiState.Success).data
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
                                    Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(pet.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            StatusBadge(pet.species, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                            
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))

                            DetailRow(icon = Icons.Default.Category, label = "Raza", value = pet.breed)
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Cake, label = "Edad", value = "${pet.age} años")
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Person, label = "ID Dueño", value = "${pet.ownerId}")
                        }
                    }
                }

                if (showDeleteDialog) {
                    ConfirmDeleteDialog(
                        itemName = pet.name,
                        onConfirm = {
                            viewModel.deletePet(id)
                            onDeleted()
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    id: Int?,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: PetViewModel = hiltViewModel()
) {
    var ownerId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadPet(id)
        }
    }

    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val pet = (detailState as UiState.Success).data
            ownerId = pet.ownerId.toString()
            name = pet.name
            species = pet.species
            breed = pet.breed
            age = pet.age.toString()
        }
    }

    LaunchedEffect(formState) {
        if (formState is UiState.Success) {
            viewModel.resetFormState()
            onSaved()
            onNavigateBack()
        }
    }

    TrackPetsScaffold(
        title = if (id == null) "Nueva Mascota" else "Editar Mascota",
        navigationIcon = {
            IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = ownerId, 
                onValueChange = { ownerId = it }, 
                label = { Text("ID del Dueño") }, 
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("Nombre") }, 
                leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = species, 
                onValueChange = { species = it }, 
                label = { Text("Tipo (Perro, Gato...)") }, 
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = breed, 
                onValueChange = { breed = it }, 
                label = { Text("Raza") }, 
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val pet = Pet(
                        id = id ?: 0,
                        ownerId = ownerId.trim().toIntOrNull() ?: 0,
                        name = name.trim(),
                        species = species.trim(),
                        breed = breed.trim(),
                        age = age.trim().toIntOrNull() ?: 0
                    )
                    viewModel.savePet(pet)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formState !is UiState.Loading && (ownerId.toIntOrNull() ?: 0) > 0
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
