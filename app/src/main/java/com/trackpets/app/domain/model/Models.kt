package com.trackpets.app.domain.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isStaff: Boolean = false,
    val isActive: Boolean = true,
    val dateJoined: String = ""
)

data class Owner(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

data class Pet(
    val id: Int = 0,
    val ownerId: Int = 0,
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val age: Int = 0
)

data class Device(
    val id: Int = 0,
    val ownerId: Int = 0,
    val imei: String = "",
    val isActive: Boolean = true,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val model: String = "",
    val installationDate: String = "",
    val renewalDate: String = ""
)

data class Geofence(
    val id: Int = 0,
    val petId: Int = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Double = 0.0
)

data class Alert(
    val id: Int = 0,
    val petId: Int = 0,
    val type: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: String? = null
)
