package com.trackpets.app.data.remote

import com.trackpets.app.data.dto.auth.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponse>

    @POST("auth/token/verify/")
    suspend fun verifyToken(@Body request: Map<String, String>): Response<Unit>

    @POST("auth/logout/")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>
}
