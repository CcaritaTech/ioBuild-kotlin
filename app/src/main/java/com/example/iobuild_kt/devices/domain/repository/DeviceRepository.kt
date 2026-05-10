package com.example.iobuild_kt.devices.domain.repository

import com.example.iobuild_kt.devices.domain.model.Device

interface DeviceRepository {
    suspend fun getDevices(): Result<List<Device>>
    suspend fun getDeviceById(id: Int): Result<Device>
    suspend fun createDevice(device: Device): Result<Device>
    suspend fun updateDevice(device: Device): Result<Device>
    suspend fun deleteDevice(id: Int): Result<Unit>
}
