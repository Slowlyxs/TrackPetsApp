package com.trackpets.app.data.dto.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val access: String,
    val refresh: String,
    @SerializedName("user_id") val userId: Int,
    val username: String,
    val email: String,
    @SerializedName("is_staff") val isStaff: Boolean
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val password2: String
)

data class RegisterResponse(
    val access: String,
    val refresh: String,
    @SerializedName("user_id") val userId: Int,
    val username: String,
    val email: String,
    @SerializedName("is_staff") val isStaff: Boolean
)

data class RefreshRequest(
    val refresh: String
)

data class RefreshResponse(
    val access: String,
    val refresh: String
)

data class LogoutRequest(
    val refresh: String
)
