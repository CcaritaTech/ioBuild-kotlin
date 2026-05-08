package com.example.iobuild_kt.dashboard.data.repository

import com.example.iobuild_kt.dashboard.data.api.AnalyticsApiService
import com.example.iobuild_kt.dashboard.data.dto.toDomain
import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard
import com.example.iobuild_kt.dashboard.domain.repository.AnalyticsRepository
import java.io.IOException

class AnalyticsRepositoryImpl(
    private val api: AnalyticsApiService
) : AnalyticsRepository {

    override suspend fun getBuilderDashboard(userId: Int): Result<BuilderDashboard> {
        return try {
            val dto = api.getBuilderDashboard(userId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            val message = when (e) {
                is IOException -> "Error de conexión. Verifica tu internet."
                else -> "Error al cargar el dashboard. Intenta de nuevo."
            }
            Result.failure(Throwable(message))
        }
    }
}
