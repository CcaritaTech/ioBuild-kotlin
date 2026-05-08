package com.example.iobuild_kt.dashboard.domain.repository

import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard

interface AnalyticsRepository {
    suspend fun getBuilderDashboard(userId: Int): Result<BuilderDashboard>
}
