package com.trackpets.app.domain.usecases

import com.trackpets.app.data.dto.PaginatedResponse
import com.trackpets.app.domain.model.*
import com.trackpets.app.domain.repository.*
import javax.inject.Inject

// Owner Use Cases
class GetOwnersUseCase @Inject constructor(private val repository: OwnerRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<Owner>> = repository.getAll(page, search)
}
class GetOwnerByIdUseCase @Inject constructor(private val repository: OwnerRepository) {
    suspend operator fun invoke(id: Int): Result<Owner> = repository.getById(id)
}
class CreateOwnerUseCase @Inject constructor(private val repository: OwnerRepository) {
    suspend operator fun invoke(owner: Owner): Result<Owner> = repository.create(owner)
}
class UpdateOwnerUseCase @Inject constructor(private val repository: OwnerRepository) {
    suspend operator fun invoke(id: Int, owner: Owner): Result<Owner> = repository.update(id, owner)
}
class DeleteOwnerUseCase @Inject constructor(private val repository: OwnerRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}

// Pet Use Cases
class GetPetsUseCase @Inject constructor(private val repository: PetRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<Pet>> = repository.getAll(page, search)
}
class GetPetByIdUseCase @Inject constructor(private val repository: PetRepository) {
    suspend operator fun invoke(id: Int): Result<Pet> = repository.getById(id)
}
class CreatePetUseCase @Inject constructor(private val repository: PetRepository) {
    suspend operator fun invoke(pet: Pet): Result<Pet> = repository.create(pet)
}
class UpdatePetUseCase @Inject constructor(private val repository: PetRepository) {
    suspend operator fun invoke(id: Int, pet: Pet): Result<Pet> = repository.update(id, pet)
}
class DeletePetUseCase @Inject constructor(private val repository: PetRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}

// Device Use Cases
class GetDevicesUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<Device>> = repository.getAll(page, search)
}
class GetDeviceByIdUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(id: Int): Result<Device> = repository.getById(id)
}
class CreateDeviceUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(device: Device): Result<Device> = repository.create(device)
}
class UpdateDeviceUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(id: Int, device: Device): Result<Device> = repository.update(id, device)
}
class DeleteDeviceUseCase @Inject constructor(private val repository: DeviceRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}

// Geofence Use Cases
class GetGeofencesUseCase @Inject constructor(private val repository: GeofenceRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<Geofence>> = repository.getAll(page, search)
}
class GetGeofenceByIdUseCase @Inject constructor(private val repository: GeofenceRepository) {
    suspend operator fun invoke(id: Int): Result<Geofence> = repository.getById(id)
}
class CreateGeofenceUseCase @Inject constructor(private val repository: GeofenceRepository) {
    suspend operator fun invoke(geofence: Geofence): Result<Geofence> = repository.create(geofence)
}
class UpdateGeofenceUseCase @Inject constructor(private val repository: GeofenceRepository) {
    suspend operator fun invoke(id: Int, geofence: Geofence): Result<Geofence> = repository.update(id, geofence)
}
class DeleteGeofenceUseCase @Inject constructor(private val repository: GeofenceRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}

// Alert Use Cases
class GetAlertsUseCase @Inject constructor(private val repository: AlertRepository) {
    suspend operator fun invoke(page: Int = 1, search: String? = null): Result<PaginatedResponse<Alert>> = repository.getAll(page, search)
}
class GetAlertByIdUseCase @Inject constructor(private val repository: AlertRepository) {
    suspend operator fun invoke(id: Int): Result<Alert> = repository.getById(id)
}
class CreateAlertUseCase @Inject constructor(private val repository: AlertRepository) {
    suspend operator fun invoke(alert: Alert): Result<Alert> = repository.create(alert)
}
class UpdateAlertUseCase @Inject constructor(private val repository: AlertRepository) {
    suspend operator fun invoke(id: Int, alert: Alert): Result<Alert> = repository.update(id, alert)
}
class DeleteAlertUseCase @Inject constructor(private val repository: AlertRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.delete(id)
}
