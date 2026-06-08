package com.trackpets.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackpets.app.domain.usecases.auth.LoginUseCase
import com.trackpets.app.domain.usecases.auth.RegisterUseCase
import com.trackpets.app.presentation.uiState.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState: StateFlow<UiState<Unit>> = _registerState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Usuario y contraseña son requeridos")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = loginUseCase(username, password)
            result.fold(
                onSuccess = { _loginState.value = UiState.Success(Unit) },
                onFailure = { _loginState.value = UiState.Error(it.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun register(username: String, email: String, password: String, passwordConfirm: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = UiState.Error("Todos los campos son requeridos")
            return
        }
        if (password != passwordConfirm) {
            _registerState.value = UiState.Error("Las contraseñas no coinciden")
            return
        }
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val result = registerUseCase(username, email, password, passwordConfirm)
            result.fold(
                onSuccess = { _registerState.value = UiState.Success(Unit) },
                onFailure = { _registerState.value = UiState.Error(it.message ?: "Error al registrarse") }
            )
        }
    }
    
    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }
    
    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }
}
