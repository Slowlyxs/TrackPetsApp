package com.trackpets.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.trackpets.app.data.dto.*
import com.trackpets.app.data.remote.*
import com.trackpets.app.domain.model.*
import com.trackpets.app.domain.repository.*
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────
// TAG fijo para filtrar en Logcat: adb logcat -s TRACKPETS_HTTP
// ─────────────────────────────────────────────────────────────────
private const val TAG = "TRACKPETS_HTTP"
private val gson = Gson()

private fun <T> logRequest(module: String, method: String, endpoint: String, dto: T) {
    val json = gson.toJson(dto)
    Log.d(TAG, "┌─────────────────────────────────────")
    Log.d(TAG, "│ [$module] $method $endpoint")
    Log.d(TAG, "│ REQUEST BODY: $json")
    Log.d(TAG, "└─────────────────────────────────────")
}

private fun logResponse(module: String, code: Int, body: String?) {
    if (code in 200..299) {
        Log.d(TAG, "✅ [$module] HTTP $code → $body")
    } else {
        Log.e(TAG, "❌ [$module] HTTP $code ERROR → $body")
    }
}


class UserRepositoryImpl @Inject constructor(private val api: UserApiService) : UserRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<User>> = try {
        val response = api.getUsers(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<User> = try {
        val response = api.getUserById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: User, password: String): Result<User> = try {
        val dto = UserDto(username = entity.username, email = entity.email, firstName = entity.firstName, lastName = entity.lastName, isStaff = entity.isStaff, isActive = entity.isActive)
        val response = api.createUser(dto)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun update(id: Int, entity: User): Result<User> = try {
        val dto = UserDto(id = entity.id, username = entity.username, email = entity.email, firstName = entity.firstName, lastName = entity.lastName, isStaff = entity.isStaff, isActive = entity.isActive)
        val response = api.updateUser(id, dto)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deleteUser(id)
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getProfile(): Result<User> = try {
        val response = api.getProfile()
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateProfile(data: Map<String, String>): Result<User> = try {
        val response = api.updateProfile(data)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun changePassword(data: Map<String, String>): Result<Map<String, String>> = try {
        val response = api.changePassword(data)
        if (response.isSuccessful) Result.success(response.body()!!)
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun toggleActive(id: Int): Result<Map<String, Any>> = try {
        val response = api.toggleActive(id)
        if (response.isSuccessful) Result.success(response.body()!!)
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getStats(): Result<Map<String, Int>> = try {
        val response = api.getStats()
        if (response.isSuccessful) Result.success(response.body()!!)
        else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}

class OwnerRepositoryImpl @Inject constructor(private val api: OwnerApiService) : OwnerRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<Owner>> = try {
        val response = api.getOwners(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<Owner> = try {
        val response = api.getOwnerById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain()) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: Owner): Result<Owner> = try {
        val dto = OwnerDto.fromDomain(entity)
        logRequest("OWNER", "POST", "owners/", dto)
        val response = api.createOwner(dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("OWNER", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[OWNER] create exception: ${e.message}"); Result.failure(e) }

    override suspend fun update(id: Int, entity: Owner): Result<Owner> = try {
        val dto = OwnerDto.fromDomain(entity)
        logRequest("OWNER", "PUT", "owners/$id/", dto)
        val response = api.updateOwner(id, dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("OWNER", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[OWNER] update exception: ${e.message}"); Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deleteOwner(id)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}

class PetRepositoryImpl @Inject constructor(private val api: PetApiService) : PetRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<Pet>> = try {
        val response = api.getPets(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<Pet> = try {
        val response = api.getPetById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain()) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: Pet): Result<Pet> = try {
        val dto = PetDto.fromDomain(entity)
        logRequest("PET", "POST", "pets/", dto)
        val response = api.createPet(dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("PET", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[PET] create exception: ${e.message}"); Result.failure(e) }

    override suspend fun update(id: Int, entity: Pet): Result<Pet> = try {
        val dto = PetDto.fromDomain(entity)
        logRequest("PET", "PUT", "pets/$id/", dto)
        val response = api.updatePet(id, dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("PET", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[PET] update exception: ${e.message}"); Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deletePet(id)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}

class DeviceRepositoryImpl @Inject constructor(private val api: DeviceApiService) : DeviceRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<Device>> = try {
        val response = api.getDevices(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<Device> = try {
        val response = api.getDeviceById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain()) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: Device): Result<Device> = try {
        val dto = DeviceDto.fromDomain(entity)
        logRequest("DEVICE", "POST", "devices/", dto)
        val response = api.createDevice(dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("DEVICE", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[DEVICE] create exception: ${e.message}"); Result.failure(e) }

    override suspend fun update(id: Int, entity: Device): Result<Device> = try {
        val dto = DeviceDto.fromDomain(entity)
        logRequest("DEVICE", "PUT", "devices/$id/", dto)
        val response = api.updateDevice(id, dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("DEVICE", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[DEVICE] update exception: ${e.message}"); Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deleteDevice(id)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}

class GeofenceRepositoryImpl @Inject constructor(private val api: GeofenceApiService) : GeofenceRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<Geofence>> = try {
        val response = api.getGeofences(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<Geofence> = try {
        val response = api.getGeofenceById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain()) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: Geofence): Result<Geofence> = try {
        val dto = GeofenceDto.fromDomain(entity)
        logRequest("GEOFENCE", "POST", "geofences/", dto)
        val response = api.createGeofence(dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("GEOFENCE", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[GEOFENCE] create exception: ${e.message}"); Result.failure(e) }

    override suspend fun update(id: Int, entity: Geofence): Result<Geofence> = try {
        val dto = GeofenceDto.fromDomain(entity)
        logRequest("GEOFENCE", "PUT", "geofences/$id/", dto)
        val response = api.updateGeofence(id, dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("GEOFENCE", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[GEOFENCE] update exception: ${e.message}"); Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deleteGeofence(id)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}

class AlertRepositoryImpl @Inject constructor(private val api: AlertApiService) : AlertRepository {
    override suspend fun getAll(page: Int, search: String?): Result<PaginatedResponse<Alert>> = try {
        val response = api.getAlerts(page, search)
        if (response.isSuccessful) {
            val body = response.body()!!
            Result.success(PaginatedResponse(body.count, body.next, body.previous, body.results.map { it.toDomain() }))
        } else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getById(id: Int): Result<Alert> = try {
        val response = api.getAlertById(id)
        if (response.isSuccessful) Result.success(response.body()!!.toDomain()) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun create(entity: Alert): Result<Alert> = try {
        val dto = AlertDto.fromDomain(entity)
        logRequest("ALERT", "POST", "alerts/", dto)
        val response = api.createAlert(dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("ALERT", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[ALERT] create exception: ${e.message}"); Result.failure(e) }

    override suspend fun update(id: Int, entity: Alert): Result<Alert> = try {
        val dto = AlertDto.fromDomain(entity)
        logRequest("ALERT", "PUT", "alerts/$id/", dto)
        val response = api.updateAlert(id, dto)
        val errorBody = if (!response.isSuccessful) response.errorBody()?.string() else null
        logResponse("ALERT", response.code(), errorBody ?: response.body()?.toString())
        if (response.isSuccessful) Result.success(response.body()!!.toDomain())
        else Result.failure(Exception("Error ${response.code()} - $errorBody"))
    } catch (e: Exception) { Log.e(TAG, "[ALERT] update exception: ${e.message}"); Result.failure(e) }

    override suspend fun delete(id: Int): Result<Unit> = try {
        val response = api.deleteAlert(id)
        if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${response.code()} - ${response.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}
