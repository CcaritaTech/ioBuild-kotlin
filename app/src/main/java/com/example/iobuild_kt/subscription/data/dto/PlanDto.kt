package com.example.iobuild_kt.subscription.data.dto

data class PlanDto(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String = "",
    val features: List<String> = emptyList(),
    val maxDevices: Int = 0,
    val maxAdministrators: Int = 0,
    val supportLevel: String = "",
    val hasAPI: Boolean = false,
    val hasAnalytics: Boolean = false
)
