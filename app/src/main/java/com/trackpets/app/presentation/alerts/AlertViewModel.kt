package com.trackpets.app.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.Alert
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
class AlertViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val getAlertByIdUseCase: GetAlertByIdUseCase,
    private val createAlertUseCase: CreateAlertUseCase,
    private val updateAlertUseCase: UpdateAlertUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<Alert>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<Alert>>> = _listState.asStateFlow()
    private val _detailState = MutableStateFlow<UiState<Alert>>(UiState.Idle)
    val detailState: StateFlow<UiState<Alert>> = _detailState.asStateFlow()
    private val _formState = MutableStateFlow<UiState<Alert>>(UiState.Idle)
    val formState: StateFlow<UiState<Alert>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<Alert>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    init { loadItems() }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) { currentPage = 1; currentList.clear(); _listState.value = UiState.Loading }
        currentSearch = search
        viewModelScope.launch {
            getAlertsUseCase(currentPage, currentSearch).fold(
                onSuccess = { response ->
                    if (currentPage == 1) currentList.clear()
                    currentList.addAll(response.results)
                    _listState.value = if (currentList.isEmpty()) UiState.Empty else UiState.Success(response.copy(results = currentList.toList()))
                    isLoadingMore = false
                },
                onFailure = { _listState.value = UiState.Error(it.message ?: "Error al cargar alertas"); isLoadingMore = false }
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

    fun loadAlert(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            getAlertByIdUseCase(id).fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = { _detailState.value = UiState.Error(it.message ?: "Error al cargar alerta") }
            )
        }
    }

    fun saveAlert(alert: Alert) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (alert.id == 0) createAlertUseCase(alert) else updateAlertUseCase(alert.id, alert)
            result.fold(
                onSuccess = { 
                    _formState.value = UiState.Success(it) 
                    loadItems(refresh = true)
                },
                onFailure = { _formState.value = UiState.Error(it.message ?: "Error al guardar") }
            )
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch { if (deleteAlertUseCase(id).isSuccess) loadItems(refresh = true) }
    }

    fun refreshList() { loadItems(search = currentSearch, refresh = true) }
    fun resetFormState() { _formState.value = UiState.Idle }
}
