package com.example.iobuild_kt.devices.domain.usecase

import com.example.iobuild_kt.devices.domain.repository.DeviceRepository

class GetDeviceByIdUseCase(private val repository: DeviceRepository) {
    suspend operator fun invoke(id: Int) = repository.getDeviceById(id)
}
