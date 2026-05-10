package com.example.iobuild_kt.devices.domain.model

data class Device(
    val id: Int = 0,
    val name: String,
    val type: String = "Temperature",
    val location: String = "",
    val macAddress: String = "",
    val projectId: Int = 1,
    val status: String = "Online"
)
