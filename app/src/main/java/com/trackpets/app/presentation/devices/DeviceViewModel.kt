package com.trackpets.app.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.Device
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
class DeviceViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val getDeviceByIdUseCase: GetDeviceByIdUseCase,
    private val createDeviceUseCase: CreateDeviceUseCase,
    private val updateDeviceUseCase: UpdateDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<Device>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<Device>>> = _listState.asStateFlow()
    private val _detailState = MutableStateFlow<UiState<Device>>(UiState.Idle)
    val detailState: StateFlow<UiState<Device>> = _detailState.asStateFlow()
    private val _formState = MutableStateFlow<UiState<Device>>(UiState.Idle)
    val formState: StateFlow<UiState<Device>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<Device>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    init { loadItems() }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) { currentPage = 1; currentList.clear(); _listState.value = UiState.Loading }
        currentSearch = search
        viewModelScope.launch {
            getDevicesUseCase(currentPage, currentSearch).fold(
                onSuccess = { response ->
                    currentList.addAll(response.results)
                    _listState.value = if (currentList.isEmpty()) UiState.Empty else UiState.Success(response.copy(results = currentList.toList()))
                    isLoadingMore = false
                },
                onFailure = { _listState.value = UiState.Error(it.message ?: "Error al cargar dispositivos"); isLoadingMore = false }
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

    fun loadDevice(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            getDeviceByIdUseCase(id).fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = { _detailState.value = UiState.Error(it.message ?: "Error al cargar dispositivo") }
            )
        }
    }

    fun saveDevice(device: Device) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (device.id == 0) createDeviceUseCase(device) else updateDeviceUseCase(device.id, device)
            result.fold(
                onSuccess = { _formState.value = UiState.Success(it) },
                onFailure = { _formState.value = UiState.Error(it.message ?: "Error al guardar") }
            )
        }
    }

    fun deleteDevice(id: Int) {
        viewModelScope.launch { if (deleteDeviceUseCase(id).isSuccess) loadItems(refresh = true) }
    }

    fun refreshList() { loadItems(search = currentSearch, refresh = true) }
    fun resetFormState() { _formState.value = UiState.Idle }
}
