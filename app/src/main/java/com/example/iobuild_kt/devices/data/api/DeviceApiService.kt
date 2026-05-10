package com.example.iobuild_kt.devices.data.api

import com.example.iobuild_kt.devices.data.dto.DeviceDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateDeviceRequest(
    val name: String, val type: String, val location: String = "",
    val macAddress: String = "", val projectId: Int = 1, val status: String = "Online"
)

data class UpdateDeviceRequest(
    val name: String, val type: String, val location: String = "",
    val macAddress: String = "", val projectId: Int = 1, val status: String = "Online"
)

interface DeviceApiService {
    @GET("devices")
    suspend fun getAllDevices(): List<DeviceDto>

    @GET("devices/{id}")
    suspend fun getDeviceById(@Path("id") id: Int): DeviceDto

    @POST("devices")
    suspend fun createDevice(@Body request: CreateDeviceRequest): DeviceDto

    @PUT("devices/{id}")
    suspend fun updateDevice(@Path("id") id: Int, @Body request: UpdateDeviceRequest): DeviceDto

    @DELETE("devices/{id}")
    suspend fun deleteDevice(@Path("id") id: Int)
}
