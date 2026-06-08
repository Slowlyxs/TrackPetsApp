package com.trackpets.app.data.repository

import com.trackpets.app.data.datastore.TokenDataStore
import com.trackpets.app.data.dto.auth.LoginRequest
import com.trackpets.app.data.dto.auth.LogoutRequest
import com.trackpets.app.data.dto.auth.RefreshRequest
import com.trackpets.app.data.dto.auth.RegisterRequest
import com.trackpets.app.data.remote.AuthApiService
import com.trackpets.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Provider

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenDataStore.saveTokens(body.access, body.refresh)
                    tokenDataStore.saveUserInfo(body.userId, body.username, body.email, body.isStaff)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, email: String, password: String, password2: String): Result<Unit> {
        return try {
            val response = authApiService.register(RegisterRequest(username, email, password, password2))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenDataStore.saveTokens(body.access, body.refresh)
                    tokenDataStore.saveUserInfo(body.userId, body.username, body.email, body.isStaff)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken().firstOrNull()
            if (refreshToken != null) {
                authApiService.logout(LogoutRequest(refreshToken))
            }
            tokenDataStore.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenDataStore.clearAll()
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return tokenDataStore.isLoggedIn()
    }
}
