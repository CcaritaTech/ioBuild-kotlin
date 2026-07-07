package com.example.iobuild_kt.subscription.domain.model

data class Plan(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String = "",
    val features: List<String> = emptyList(),
    val maxDevices: Int = 0,
    val maxAdministrators: Int = 0,
    val supportLevel: String = "",
    val hasApi: Boolean = false,
    val hasAnalytics: Boolean = false
)
