package com.trackpets.app.domain.usecases.auth

import com.trackpets.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        return repository.login(username, password)
    }
}

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, email: String, password: String, password2: String): Result<Unit> {
        return repository.register(username, email, password, password2)
    }
}

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}

class GetCurrentUserUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isLoggedIn()
    }
}
