package com.trackpets.app.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackpets.app.domain.model.User
import com.trackpets.app.presentation.components.*
import com.trackpets.app.presentation.uiState.UiState

@Composable
fun UserListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val isPermissionDenied by viewModel.isPermissionDenied.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    TrackPetsScaffold(
        title = "Usuarios",
        floatingActionButton = {
            if (!isPermissionDenied) {
                FloatingActionButton(onClick = onNavigateToCreate, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        }
    ) { padding ->
        if (isPermissionDenied) {
            // Friendly permission denied UI
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Sin permisos",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Acceso Restringido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "No tienes permisos para acceder a este módulo.\n\nSolo los administradores (staff) pueden gestionar usuarios.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = { viewModel.loadItems(refresh = true) }) {
                    Text("Reintentar")
                }
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.padding(16.dp)
                )

                when (listState) {
                    is UiState.Loading -> LoadingIndicator()
                    is UiState.Error -> ErrorView((listState as UiState.Error).message, { viewModel.loadItems(refresh = true) })
                    is UiState.Empty -> EmptyStateView("No hay usuarios", "Añade un nuevo usuario usando el botón +")
                    is UiState.Success -> {
                        val data = (listState as UiState.Success).data
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(data.results, key = { it.id }) { user ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onNavigateToDetail(user.id) },
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(user.username, style = MaterialTheme.typography.titleMedium)
                                        Text(user.email, style = MaterialTheme.typography.bodyMedium)
                                        Text("Activo: ${if(user.isActive) "Sí" else "No"}", style = MaterialTheme.typography.bodySmall)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    id: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onDeleted: () -> Unit = onNavigateBack,
    viewModel: UserViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) { viewModel.loadUser(id) }

    TrackPetsScaffold(
        title = "Detalle de Usuario",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
        actions = {
            IconButton(onClick = { onNavigateToEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
        }
    ) { padding ->
        when (detailState) {
            is UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView((detailState as UiState.Error).message, { viewModel.loadUser(id) }, Modifier.padding(padding))
            is UiState.Success -> {
                val user = (detailState as UiState.Success).data
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    Text("Usuario: ${user.username}", style = MaterialTheme.typography.titleLarge)
                    Text("Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
                    Text("Nombre: ${user.firstName} ${user.lastName}", style = MaterialTheme.typography.bodyLarge)
                    Text("Activo: ${if (user.isActive) "Sí" else "No"}", style = MaterialTheme.typography.bodyLarge)
                    Text("Staff: ${if (user.isStaff) "Sí" else "No"}", style = MaterialTheme.typography.bodyLarge)
                    Text("Unido: ${user.dateJoined}", style = MaterialTheme.typography.bodyLarge)
                }
                if (showDeleteDialog) {
                    ConfirmDeleteDialog(itemName = user.username, onConfirm = { viewModel.deleteUser(id); onDeleted() }, onDismiss = { showDeleteDialog = false })
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    id: Int?,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: UserViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var isStaff by remember { mutableStateOf(false) }

    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(id) { if (id != null) viewModel.loadUser(id) }
    LaunchedEffect(detailState) {
        if (id != null && detailState is UiState.Success) {
            val user = (detailState as UiState.Success).data
            username = user.username; email = user.email; firstName = user.firstName; lastName = user.lastName; isActive = user.isActive; isStaff = user.isStaff
        }
    }
    LaunchedEffect(formState) { if (formState is UiState.Success) { viewModel.resetFormState(); onSaved(); onNavigateBack() } }

    TrackPetsScaffold(
        title = if (id == null) "Nuevo Usuario" else "Editar Usuario",
        navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isActive, onCheckedChange = { isActive = it }); Text("Activo")
                Spacer(Modifier.width(16.dp))
                Checkbox(checked = isStaff, onCheckedChange = { isStaff = it }); Text("Staff")
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.saveUser(User(id = id ?: 0, username = username, email = email, firstName = firstName, lastName = lastName, isActive = isActive, isStaff = isStaff)) },
                modifier = Modifier.fillMaxWidth(), enabled = formState !is UiState.Loading
            ) { if (formState is UiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp)) else Text("Guardar") }
            if (formState is UiState.Error) { Text((formState as UiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}
