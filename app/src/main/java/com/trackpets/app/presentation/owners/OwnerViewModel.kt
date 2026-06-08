package com.trackpets.app.presentation.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.Owner
import com.trackpets.app.domain.usecases.*
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
class OwnerViewModel @Inject constructor(
    private val getOwnersUseCase: GetOwnersUseCase,
    private val getOwnerByIdUseCase: GetOwnerByIdUseCase,
    private val createOwnerUseCase: CreateOwnerUseCase,
    private val updateOwnerUseCase: UpdateOwnerUseCase,
    private val deleteOwnerUseCase: DeleteOwnerUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<Owner>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<Owner>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<Owner>>(UiState.Idle)
    val detailState: StateFlow<UiState<Owner>> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow<UiState<Owner>>(UiState.Idle)
    val formState: StateFlow<UiState<Owner>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<Owner>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    init { loadItems() }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            currentList.clear()
            _listState.value = UiState.Loading
        }
        currentSearch = search
        viewModelScope.launch {
            val result = getOwnersUseCase(currentPage, currentSearch)
            result.fold(
                onSuccess = { response ->
                    currentList.addAll(response.results)
                    _listState.value = if (currentList.isEmpty()) UiState.Empty
                    else UiState.Success(response.copy(results = currentList.toList()))
                    isLoadingMore = false
                },
                onFailure = { _listState.value = UiState.Error(it.message ?: "Error al cargar dueños"); isLoadingMore = false }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            loadItems(search = query.ifBlank { null }, refresh = true)
        }
    }

    fun loadMore() {
        if (isLoadingMore) return
        val state = _listState.value
        if (state is UiState.Success && state.data.next != null) { isLoadingMore = true; currentPage++; loadItems(currentSearch) }
    }

    fun loadOwner(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            getOwnerByIdUseCase(id).fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = { _detailState.value = UiState.Error(it.message ?: "Error al cargar dueño") }
            )
        }
    }

    fun saveOwner(owner: Owner) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (owner.id == 0) createOwnerUseCase(owner) else updateOwnerUseCase(owner.id, owner)
            result.fold(
                onSuccess = { _formState.value = UiState.Success(it) },
                onFailure = { _formState.value = UiState.Error(it.message ?: "Error al guardar") }
            )
        }
    }

    fun deleteOwner(id: Int) {
        viewModelScope.launch {
            if (deleteOwnerUseCase(id).isSuccess) loadItems(refresh = true)
        }
    }

    fun refreshList() { loadItems(search = currentSearch, refresh = true) }
    fun resetFormState() { _formState.value = UiState.Idle }
}
