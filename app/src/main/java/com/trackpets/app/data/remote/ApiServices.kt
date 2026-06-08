package com.trackpets.app.data.remote

import com.trackpets.app.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @GET("users/")
    suspend fun getUsers(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<UserDto>>
    @GET("users/{id}/")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>
    @POST("users/")
    suspend fun createUser(@Body user: UserDto): Response<UserDto>
    @PUT("users/{id}/")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDto): Response<UserDto>
    @DELETE("users/{id}/")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
    @GET("users/profile/")
    suspend fun getProfile(): Response<UserDto>
    @PATCH("users/profile/")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<UserDto>
    @POST("users/change-password/")
    suspend fun changePassword(@Body data: Map<String, String>): Response<Map<String, String>>
    @POST("users/{id}/toggle-active/")
    suspend fun toggleActive(@Path("id") id: Int): Response<Map<String, Any>>
    @GET("users/stats/")
    suspend fun getStats(): Response<Map<String, Int>>
}

interface OwnerApiService {
    @GET("owners/")
    suspend fun getOwners(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<OwnerDto>>
    @GET("owners/{id}/")
    suspend fun getOwnerById(@Path("id") id: Int): Response<OwnerDto>
    @POST("owners/")
    suspend fun createOwner(@Body owner: OwnerDto): Response<OwnerDto>
    @PUT("owners/{id}/")
    suspend fun updateOwner(@Path("id") id: Int, @Body owner: OwnerDto): Response<OwnerDto>
    @DELETE("owners/{id}/")
    suspend fun deleteOwner(@Path("id") id: Int): Response<Unit>
}

interface PetApiService {
    @GET("pets/")
    suspend fun getPets(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<PetDto>>
    @GET("pets/{id}/")
    suspend fun getPetById(@Path("id") id: Int): Response<PetDto>
    @POST("pets/")
    suspend fun createPet(@Body pet: PetDto): Response<PetDto>
    @PUT("pets/{id}/")
    suspend fun updatePet(@Path("id") id: Int, @Body pet: PetDto): Response<PetDto>
    @DELETE("pets/{id}/")
    suspend fun deletePet(@Path("id") id: Int): Response<Unit>
}

interface DeviceApiService {
    @GET("devices/")
    suspend fun getDevices(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<DeviceDto>>
    @GET("devices/{id}/")
    suspend fun getDeviceById(@Path("id") id: Int): Response<DeviceDto>
    @POST("devices/")
    suspend fun createDevice(@Body device: DeviceDto): Response<DeviceDto>
    @PUT("devices/{id}/")
    suspend fun updateDevice(@Path("id") id: Int, @Body device: DeviceDto): Response<DeviceDto>
    @DELETE("devices/{id}/")
    suspend fun deleteDevice(@Path("id") id: Int): Response<Unit>
}

interface GeofenceApiService {
    @GET("geofences/")
    suspend fun getGeofences(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<GeofenceDto>>
    @GET("geofences/{id}/")
    suspend fun getGeofenceById(@Path("id") id: Int): Response<GeofenceDto>
    @POST("geofences/")
    suspend fun createGeofence(@Body geofence: GeofenceDto): Response<GeofenceDto>
    @PUT("geofences/{id}/")
    suspend fun updateGeofence(@Path("id") id: Int, @Body geofence: GeofenceDto): Response<GeofenceDto>
    @DELETE("geofences/{id}/")
    suspend fun deleteGeofence(@Path("id") id: Int): Response<Unit>
}

interface AlertApiService {
    @GET("alerts/")
    suspend fun getAlerts(@Query("page") page: Int = 1, @Query("search") search: String? = null, @Query("page_size") pageSize: Int = 10): Response<PaginatedResponse<AlertDto>>
    @GET("alerts/{id}/")
    suspend fun getAlertById(@Path("id") id: Int): Response<AlertDto>
    @POST("alerts/")
    suspend fun createAlert(@Body alert: AlertDto): Response<AlertDto>
    @PUT("alerts/{id}/")
    suspend fun updateAlert(@Path("id") id: Int, @Body alert: AlertDto): Response<AlertDto>
    @DELETE("alerts/{id}/")
    suspend fun deleteAlert(@Path("id") id: Int): Response<Unit>
}
