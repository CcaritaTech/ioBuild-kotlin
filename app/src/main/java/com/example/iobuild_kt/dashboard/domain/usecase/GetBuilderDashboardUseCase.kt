package com.example.iobuild_kt.dashboard.domain.usecase

import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard
import com.example.iobuild_kt.dashboard.domain.repository.AnalyticsRepository

class GetBuilderDashboardUseCase(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(userId: Int): Result<BuilderDashboard> {
        return repository.getBuilderDashboard(userId)
    }
}
