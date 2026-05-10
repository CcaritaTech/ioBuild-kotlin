package com.example.iobuild_kt.devices.domain.usecase

import com.example.iobuild_kt.devices.domain.model.Device
import com.example.iobuild_kt.devices.domain.repository.DeviceRepository

class CreateDeviceUseCase(private val repository: DeviceRepository) {
    suspend operator fun invoke(device: Device) = repository.createDevice(device)
}
