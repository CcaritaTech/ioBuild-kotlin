package com.example.iobuild_kt.devices.domain.usecase

import com.example.iobuild_kt.devices.domain.repository.DeviceRepository

class GetDevicesUseCase(private val repository: DeviceRepository) {
    suspend operator fun invoke() = repository.getDevices()
}
