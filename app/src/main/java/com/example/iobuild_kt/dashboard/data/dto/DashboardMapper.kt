package com.example.iobuild_kt.dashboard.data.dto

import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard
import com.example.iobuild_kt.dashboard.domain.model.HistoricalDataPoint
import com.example.iobuild_kt.dashboard.domain.model.MonthlyOccupancyData
import com.example.iobuild_kt.dashboard.domain.model.ProjectOverview

fun BuilderDashboardDto.toDomain(): BuilderDashboard = BuilderDashboard(
    totalDevices = totalDevices,
    onlineDevices = onlineDevices,
    offlineDevices = offlineDevices,
    alertsCount = alertsCount,
    activeProjectsCount = activeProjectsCount,
    totalUnits = totalUnits,
    occupiedUnits = occupiedUnits,
    occupancyRate = occupancyRate,
    energyEfficiencyAvg = energyEfficiencyAvg,
    temperatureHistory = temperatureHistory.map { it.toDomain() },
    energyHistory = energyHistory.map { it.toDomain() },
    monthlyOccupancy = monthlyOccupancy.map { it.toDomain() },
    devicesByType = devicesByType,
    projectsOverview = projectsOverview.map { it.toDomain() }
)

fun HistoricalDataPointDto.toDomain() = HistoricalDataPoint(
    timestamp = timestamp,
    value = value,
    type = type
)

fun MonthlyOccupancyDto.toDomain() = MonthlyOccupancyData(
    month = month,
    occupancyRate = occupancyRate,
    year = year
)

fun ProjectOverviewDto.toDomain() = ProjectOverview(
    id = id,
    name = name,
    location = location,
    status = status,
    totalUnits = totalUnits,
    occupiedUnits = occupiedUnits,
    occupancyRate = occupancyRate,
    deviceCount = deviceCount
)
