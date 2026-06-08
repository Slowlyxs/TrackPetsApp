package com.trackpets.app.presentation.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.Pet
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
class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val createPetUseCase: CreatePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<PaginatedResponse<Pet>>>(UiState.Loading)
    val listState: StateFlow<UiState<PaginatedResponse<Pet>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<Pet>>(UiState.Idle)
    val detailState: StateFlow<UiState<Pet>> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow<UiState<Pet>>(UiState.Idle)
    val formState: StateFlow<UiState<Pet>> = _formState.asStateFlow()

    private var currentPage = 1
    private var currentSearch: String? = null
    private val currentList = mutableListOf<Pet>()
    private var searchJob: Job? = null
    private var isLoadingMore = false

    init {
        loadItems()
    }

    fun loadItems(search: String? = null, refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            currentList.clear()
            _listState.value = UiState.Loading
        }
        currentSearch = search
        viewModelScope.launch {
            val result = getPetsUseCase(currentPage, currentSearch)
            result.fold(
                onSuccess = { response ->
                    if (currentPage == 1) currentList.clear()
                    currentList.addAll(response.results)
                    if (currentList.isEmpty()) {
                        _listState.value = UiState.Empty
                    } else {
                        _listState.value = UiState.Success(response.copy(results = currentList.toList()))
                    }
                    isLoadingMore = false
                },
                onFailure = {
                    _listState.value = UiState.Error(it.message ?: "Error al cargar mascotas")
                    isLoadingMore = false
                }
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
        if (state is UiState.Success && state.data.next != null) {
            isLoadingMore = true
            currentPage++
            loadItems(currentSearch)
        }
    }

    fun loadPet(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            val result = getPetByIdUseCase(id)
            result.fold(
                onSuccess = { _detailState.value = UiState.Success(it) },
                onFailure = { _detailState.value = UiState.Error(it.message ?: "Error al cargar mascota") }
            )
        }
    }

    fun savePet(pet: Pet) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            val result = if (pet.id == 0) createPetUseCase(pet) else updatePetUseCase(pet.id, pet)
            result.fold(
                onSuccess = {
                    _formState.value = UiState.Success(it)
                },
                onFailure = { _formState.value = UiState.Error(it.message ?: "Error al guardar") }
            )
        }
    }

    fun deletePet(id: Int) {
        viewModelScope.launch {
            val result = deletePetUseCase(id)
            if (result.isSuccess) {
                loadItems(refresh = true)
            }
        }
    }

    fun refreshList() {
        loadItems(search = currentSearch, refresh = true)
    }

    fun resetFormState() {
        _formState.value = UiState.Idle
    }
}
