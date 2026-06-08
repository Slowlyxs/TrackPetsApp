package com.trackpets.app.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.User
import com.trackpets.app.domain.usecases.user.*
import com.trackpets.app.presentation.uiState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<User>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<User>>> = _listState.asStateFlow()
    private val _detailState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val detailState: StateFlow<UiState<User>> = _detailState.asStateFlow()
    private val _formState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val formState: StateFlow<UiState<User>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<User>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    // Track if the 403 error is a permissions issue
    private val _isPermissionDenied = MutableStateFlow(false)
    val isPermissionDenied: StateFlow<Boolean> = _isPermissionDenied.asStateFlow()

    init { loadItems() }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) { currentPage = 1; currentList.clear(); _listState.value = UiState.Loading }
        currentSearch = search
        viewModelScope.launch {
            getUsersUseCase(currentPage, currentSearch).fold(
                onSuccess = { response ->
                    _isPermissionDenied.value = false
                    currentList.addAll(response.results)
                    _listState.value = if (currentList.isEmpty()) UiState.Empty else UiState.Success(response.copy(results = currentList.toList()))
                    isLoadingMore = false
                },
                onFailure = { error ->
                    val message = error.message ?: "Error desconocido"
                    if (message.contains("403") || message.contains("Forbidden", ignoreCase = true)) {
                        _isPermissionDenied.value = true
                        _listState.value = UiState.Error("No tienes permisos para acceder a este módulo.\n\nSolo los administradores (staff) pueden gestionar usuarios.")
                    } else {
                        _listState.value = UiState.Error(message)
                    }
                    isLoadingMore = false
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch { delay(400); loadItems(search = query.ifBlank { null }, refresh = true) }
    }

    fun loadMore() {
        if (isLoadingMore) return
        val state = _listState.value
        if (state is UiState.Success && state.data.next != null) { isLoadingMore = true; currentPage++; loadItems(currentSearch) }
    }

    fun loadUser(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            getUserByIdUseCase(id).fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = {
                    val msg = it.message ?: "Error"
                    _detailState.value = if (msg.contains("403")) UiState.Error("No tienes permisos para ver este usuario.") else UiState.Error(msg)
                }
            )
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (user.id == 0) createUserUseCase(user) else updateUserUseCase(user.id, user)
            result.fold(
                onSuccess = { _formState.value = UiState.Success(it) },
                onFailure = {
                    val msg = it.message ?: "Error"
                    _formState.value = if (msg.contains("403")) UiState.Error("No tienes permisos de administrador para realizar esta acción.") else UiState.Error(msg)
                }
            )
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch { if (deleteUserUseCase(id).isSuccess) loadItems(refresh = true) }
    }

    fun refreshList() { loadItems(search = currentSearch, refresh = true) }
    fun resetFormState() { _formState.value = UiState.Idle }
}
