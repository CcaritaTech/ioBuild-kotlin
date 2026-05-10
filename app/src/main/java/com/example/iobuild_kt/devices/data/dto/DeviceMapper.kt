package com.example.iobuild_kt.devices.data.dto

import com.example.iobuild_kt.devices.data.api.CreateDeviceRequest
import com.example.iobuild_kt.devices.data.api.UpdateDeviceRequest
import com.example.iobuild_kt.devices.domain.model.Device

fun DeviceDto.toDomain() = Device(
    id = id, name = name, type = type, location = location,
    macAddress = macAddress, projectId = projectId, status = status
)

fun Device.toCreateRequest() = CreateDeviceRequest(
    name = name, type = type, location = location,
    macAddress = macAddress, projectId = projectId, status = status
)

fun Device.toUpdateRequest() = UpdateDeviceRequest(
    name = name, type = type, location = location,
    macAddress = macAddress, projectId = projectId, status = status
)
