package com.trackpets.app.presentation.geofences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.Geofence
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
class GeofenceViewModel @Inject constructor(
    private val getGeofencesUseCase: GetGeofencesUseCase,
    private val getGeofenceByIdUseCase: GetGeofenceByIdUseCase,
    private val createGeofenceUseCase: CreateGeofenceUseCase,
    private val updateGeofenceUseCase: UpdateGeofenceUseCase,
    private val deleteGeofenceUseCase: DeleteGeofenceUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<Geofence>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<Geofence>>> = _listState.asStateFlow()
    private val _detailState = MutableStateFlow<UiState<Geofence>>(UiState.Idle)
    val detailState: StateFlow<UiState<Geofence>> = _detailState.asStateFlow()
    private val _formState = MutableStateFlow<UiState<Geofence>>(UiState.Idle)
    val formState: StateFlow<UiState<Geofence>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<Geofence>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    init { loadItems() }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) { currentPage = 1; currentList.clear(); _listState.value = UiState.Loading }
        currentSearch = search
        viewModelScope.launch {
            getGeofencesUseCase(currentPage, currentSearch).fold(
                onSuccess = { response ->
                    if (currentPage == 1) currentList.clear()
                    currentList.addAll(response.results)
                    _listState.value = if (currentList.isEmpty()) UiState.Empty else UiState.Success(response.copy(results = currentList.toList()))
                    isLoadingMore = false
                },
                onFailure = { _listState.value = UiState.Error(it.message ?: "Error al cargar geocercas"); isLoadingMore = false }
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

    fun loadGeofence(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            getGeofenceByIdUseCase(id).fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = { _detailState.value = UiState.Error(it.message ?: "Error al cargar geocerca") }
            )
        }
    }

    fun saveGeofence(geofence: Geofence) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (geofence.id == 0) createGeofenceUseCase(geofence) else updateGeofenceUseCase(geofence.id, geofence)
            result.fold(
                onSuccess = { 
                    _formState.value = UiState.Success(it)
                    loadItems(refresh = true) // <--- Refrescar lista!
                },
                onFailure = { _formState.value = UiState.Error(it.message ?: "Error al guardar") }
            )
        }
    }

    fun deleteGeofence(id: Int) {
        viewModelScope.launch { if (deleteGeofenceUseCase(id).isSuccess) loadItems(refresh = true) }
    }

    fun refreshList() { loadItems(search = currentSearch, refresh = true) }
    fun resetFormState() { _formState.value = UiState.Idle }
}
