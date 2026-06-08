package com.trackpets.app.presentation.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.domain.usecases.auth.LogoutUseCase
import com.trackpets.app.presentation.uiState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.trackpets.app.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val logoutState: StateFlow<UiState<Unit>> = _logoutState.asStateFlow()

    val isStaff: StateFlow<Boolean> = tokenDataStore.getIsStaff()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading
            val result = logoutUseCase()
            result.fold(
                onSuccess = { _logoutState.value = UiState.Success(Unit) },
                onFailure = { _logoutState.value = UiState.Error(it.message ?: "Error al cerrar sesión") }
            )
        }
    }
}
