package com.example.iobuild_kt.dashboard.data.dto

import com.google.gson.annotations.SerializedName

data class BuilderDashboardDto(
    val totalDevices: Int,
    val onlineDevices: Int,
    val offlineDevices: Int,
    val alertsCount: Int,
    val activeProjectsCount: Int,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val occupancyRate: Double,
    val energyEfficiencyAvg: Double,
    val temperatureHistory: List<HistoricalDataPointDto>,
    val energyHistory: List<HistoricalDataPointDto>,
    val monthlyOccupancy: List<MonthlyOccupancyDto>,
    val devicesByType: Map<String, Int>,
    val projectsOverview: List<ProjectOverviewDto>
)

data class HistoricalDataPointDto(
    val timestamp: String,
    val value: Double,
    val type: String
)

data class MonthlyOccupancyDto(
    val month: String,
    val occupancyRate: Double,
    val year: Int
)

data class ProjectOverviewDto(
    val id: Int,
    val name: String,
    val location: String,
    val status: String,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val occupancyRate: Double,
    val deviceCount: Int
)
