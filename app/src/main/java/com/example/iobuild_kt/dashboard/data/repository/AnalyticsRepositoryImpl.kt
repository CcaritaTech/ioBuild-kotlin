package com.example.iobuild_kt.dashboard.data.repository

import com.example.iobuild_kt.dashboard.data.api.AnalyticsApiService
import com.example.iobuild_kt.dashboard.data.dto.toDomain
import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard
import com.example.iobuild_kt.dashboard.domain.repository.AnalyticsRepository
import retrofit2.HttpException
import java.io.IOException

private val EMPTY_DASHBOARD = BuilderDashboard(
    totalDevices = 0,
    onlineDevices = 0,
    offlineDevices = 0,
    alertsCount = 0,
    activeProjectsCount = 0,
    totalUnits = 0,
    occupiedUnits = 0,
    occupancyRate = 0.0,
    energyEfficiencyAvg = 0.0,
    temperatureHistory = emptyList(),
    energyHistory = emptyList(),
    monthlyOccupancy = emptyList(),
    devicesByType = emptyMap(),
    projectsOverview = emptyList()
)

class AnalyticsRepositoryImpl(
    private val api: AnalyticsApiService
) : AnalyticsRepository {

    override suspend fun getBuilderDashboard(userId: Int): Result<BuilderDashboard> {
        return try {
            val dto = api.getBuilderDashboard(userId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            // A builder with no projects/devices yet gets a 404 instead of an empty payload —
            // treat that as a blank dashboard rather than surfacing an error screen.
            if (e is HttpException && e.code() == 404) {
                return Result.success(EMPTY_DASHBOARD)
            }
            val message = when (e) {
                is IOException -> "Error de conexión. Verifica tu internet."
                else -> "Error al cargar el dashboard. Intenta de nuevo."
            }
            Result.failure(Throwable(message))
        }
    }
}
