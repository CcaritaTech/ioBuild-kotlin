package com.example.iobuild_kt.dashboard.domain.model

data class ProjectOverview(
    val id: Int,
    val name: String,
    val location: String,
    val status: String,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val occupancyRate: Double,
    val deviceCount: Int
)
