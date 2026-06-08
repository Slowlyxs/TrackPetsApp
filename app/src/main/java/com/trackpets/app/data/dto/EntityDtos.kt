package com.trackpets.app.data.dto

import com.google.gson.annotations.SerializedName
import com.trackpets.app.domain.model.*

data class UserDto(
    val id: Int? = null,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("is_staff") val isStaff: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("date_joined") val dateJoined: String? = null
) {
    fun toDomain() = User(
        id = id ?: 0,
        username = username,
        email = email,
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        isStaff = isStaff,
        isActive = isActive,
        dateJoined = dateJoined.orEmpty()
    )
}

data class OwnerDto(
    val id: Int? = null,
    val nombre: String,
    val email: String,
    val telefono: String
) {
    fun toDomain() = Owner(id = id ?: 0, name = nombre, email = email, phone = telefono)

    companion object {
        fun fromDomain(domain: Owner) = OwnerDto(
            id = if (domain.id == 0) null else domain.id,
            nombre = domain.name,
            email = domain.email,
            telefono = domain.phone
        )
    }
}

data class PetDto(
    val id: Int? = null,
    val owner: Int,
    val nombre: String,
    val tipo: String,
    val raza: String,
    val edad: Int
) {
    fun toDomain() = Pet(id = id ?: 0, ownerId = owner, name = nombre, species = tipo, breed = raza, age = edad)

    companion object {
        fun fromDomain(domain: Pet) = PetDto(
            id = if (domain.id == 0) null else domain.id,
            owner = domain.ownerId,
            nombre = domain.name,
            tipo = domain.species,
            raza = domain.breed,
            edad = domain.age
        )
    }
}

data class DeviceDto(
    val id: Int? = null,
    val owner: Int,
    val imei: String,
    val activo: Boolean,
    val latitud: Double,
    val longitud: Double,
    val modelo: String,
    @SerializedName("fecha_de_instalacion") val fechaInstalacion: String,
    @SerializedName("fecha_renovacion") val fechaRenovacion: String
) {
    fun toDomain() = Device(
        id = id ?: 0,
        ownerId = owner,
        imei = imei,
        isActive = activo,
        latitude = latitud,
        longitude = longitud,
        model = modelo,
        installationDate = fechaInstalacion,
        renewalDate = fechaRenovacion
    )

    companion object {
        fun fromDomain(domain: Device) = DeviceDto(
            id = if (domain.id == 0) null else domain.id,
            owner = domain.ownerId,
            imei = domain.imei,
            activo = domain.isActive,
            latitud = domain.latitude,
            longitud = domain.longitude,
            modelo = domain.model,
            fechaInstalacion = domain.installationDate,
            fechaRenovacion = domain.renewalDate
        )
    }
}

data class GeofenceDto(
    val id: Int? = null,
    val pet: Int,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val radio: Double
) {
    fun toDomain() = Geofence(id = id ?: 0, petId = pet, name = nombre, latitude = latitud, longitude = longitud, radius = radio)

    companion object {
        fun fromDomain(domain: Geofence) = GeofenceDto(
            id = if (domain.id == 0) null else domain.id,
            pet = domain.petId,
            nombre = domain.name,
            latitud = domain.latitude,
            longitud = domain.longitude,
            radio = domain.radius
        )
    }
}

data class AlertDto(
    val id: Int? = null,
    val pet: Int,
    val tipo: String,
    val descripcion: String,
    val activa: Boolean,
    val fecha: String? = null
) {
    fun toDomain() = Alert(id = id ?: 0, petId = pet, type = tipo, description = descripcion, isActive = activa, createdAt = fecha)

    companion object {
        fun fromDomain(domain: Alert) = AlertDto(
            id = if (domain.id == 0) null else domain.id,
            pet = domain.petId,
            tipo = domain.type,
            descripcion = domain.description,
            activa = domain.isActive,
            fecha = if (domain.createdAt.isNullOrBlank()) null else domain.createdAt
        )
    }
}
