package com.example.iobuild_kt.devices.data.dto

data class DeviceDto(
    val id: Int,
    val name: String,
    val type: String,
    val location: String,
    val macAddress: String,
    val projectId: Int,
    val status: String
)
