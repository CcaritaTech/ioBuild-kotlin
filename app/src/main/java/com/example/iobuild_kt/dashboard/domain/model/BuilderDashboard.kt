package com.example.iobuild_kt.dashboard.domain.model

data class BuilderDashboard(
    val totalDevices: Int,
    val onlineDevices: Int,
    val offlineDevices: Int,
    val alertsCount: Int,
    val activeProjectsCount: Int,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val occupancyRate: Double,
    val energyEfficiencyAvg: Double,
    val temperatureHistory: List<HistoricalDataPoint>,
    val energyHistory: List<HistoricalDataPoint>,
    val monthlyOccupancy: List<MonthlyOccupancyData>,
    val devicesByType: Map<String, Int>,
    val projectsOverview: List<ProjectOverview>
)
