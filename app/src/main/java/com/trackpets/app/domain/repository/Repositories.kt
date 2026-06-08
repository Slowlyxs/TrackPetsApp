package com.trackpets.app.domain.repository

import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun register(username: String, email: String, password: String, password2: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Flow<Boolean>
}

interface UserRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<User>>
    suspend fun getById(id: Int): Result<User>
    suspend fun create(entity: User, password: String): Result<User>
    suspend fun update(id: Int, entity: User): Result<User>
    suspend fun delete(id: Int): Result<Unit>
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(data: Map<String, String>): Result<User>
    suspend fun changePassword(data: Map<String, String>): Result<Map<String, String>>
    suspend fun toggleActive(id: Int): Result<Map<String, Any>>
    suspend fun getStats(): Result<Map<String, Int>>
}

interface OwnerRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<Owner>>
    suspend fun getById(id: Int): Result<Owner>
    suspend fun create(entity: Owner): Result<Owner>
    suspend fun update(id: Int, entity: Owner): Result<Owner>
    suspend fun delete(id: Int): Result<Unit>
}

interface PetRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<Pet>>
    suspend fun getById(id: Int): Result<Pet>
    suspend fun create(entity: Pet): Result<Pet>
    suspend fun update(id: Int, entity: Pet): Result<Pet>
    suspend fun delete(id: Int): Result<Unit>
}

interface DeviceRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<Device>>
    suspend fun getById(id: Int): Result<Device>
    suspend fun create(entity: Device): Result<Device>
    suspend fun update(id: Int, entity: Device): Result<Device>
    suspend fun delete(id: Int): Result<Unit>
}

interface GeofenceRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<Geofence>>
    suspend fun getById(id: Int): Result<Geofence>
    suspend fun create(entity: Geofence): Result<Geofence>
    suspend fun update(id: Int, entity: Geofence): Result<Geofence>
    suspend fun delete(id: Int): Result<Unit>
}

interface AlertRepository {
    suspend fun getAll(page: Int = 1, search: String? = null): Result<PaginatedResponse<Alert>>
    suspend fun getById(id: Int): Result<Alert>
    suspend fun create(entity: Alert): Result<Alert>
    suspend fun update(id: Int, entity: Alert): Result<Alert>
    suspend fun delete(id: Int): Result<Unit>
}
