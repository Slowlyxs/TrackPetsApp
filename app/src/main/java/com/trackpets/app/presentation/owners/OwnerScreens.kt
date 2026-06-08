package com.trackpets.app.presentation.owners

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
import com.trackpets.app.domain.model.Owner
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun OwnerListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    TrackPetsScaffold(
        title = "Dueños",
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
                is UiState.Empty -> EmptyStateView("No hay dueños", "Añade un nuevo dueño usando el botón +")
                is UiState.Success -> {
                    val data = (listState as UiState.Success).data
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(data.results, key = { it.id }) { owner ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onNavigateToDetail(owner.id) },
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
                                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(owner.name, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(owner.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(owner.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            if (data.next != null) {
                                TextButton(onClick = { viewModel.loadMore() }, modifier = Modifier.fillMaxWidth()) { Text("Cargar más...") }
                            } else { PaginationFooter(isLoading = false, hasMore = false) }
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
fun OwnerDetailScreen(
    id: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onDeleted: () -> Unit = onNavigateBack,
    viewModel: OwnerViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) { viewModel.loadOwner(id) }

    TrackPetsScaffold(
        title = "Detalle de Dueño",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
        actions = {
            IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
        }
    ) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadOwner(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val owner = (detailState as UiState.Success).data
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
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(owner.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                            
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))

                            DetailRow(icon = Icons.Default.Email, label = "Correo Electrónico", value = owner.email)
                            Spacer(Modifier.height(16.dp))
                            DetailRow(icon = Icons.Default.Phone, label = "Teléfono", value = owner.phone)
                        }
                    }
                }
                if (showDeleteDialog) {
                    ConfirmDeleteDialog(itemName = owner.name, onConfirm = { viewModel.deleteOwner(id); onDeleted() }, onDismiss = { showDeleteDialog = false })
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerFormScreen(
    id: Int?,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: OwnerViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) { if (id != null) viewModel.loadOwner(id) }
    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val owner = (detailState as UiState.Success).data
            name = owner.name; email = owner.email; phone = owner.phone
        }
    }
    LaunchedEffect(formState) {
        if (formState is UiState.Success) { viewModel.resetFormState(); onSaved(); onNavigateBack() }
    }

    TrackPetsScaffold(
        title = if (id == null) "Nuevo Dueño" else "Editar Dueño",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = name, 
                onValueChange = { name = it }, 
                label = { Text("Nombre") }, 
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = email, 
                onValueChange = { email = it }, 
                label = { Text("Email") }, 
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = phone, 
                onValueChange = { phone = it }, 
                label = { Text("Teléfono") }, 
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.saveOwner(Owner(id = id ?: 0, name = name, email = email, phone = phone)) },
                modifier = Modifier.fillMaxWidth(), enabled = formState !is UiState.Loading
            ) {
                if (formState is UiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                else Text("Guardar")
            }
            if (formState is UiState.Error) { Text((formState as UiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}
