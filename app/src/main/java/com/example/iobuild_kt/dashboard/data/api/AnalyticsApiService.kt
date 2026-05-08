package com.example.iobuild_kt.dashboard.data.api

import com.example.iobuild_kt.dashboard.data.dto.BuilderDashboardDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnalyticsApiService {
    @GET("analytics/metrics/{userId}")
    suspend fun getBuilderDashboard(
        @Path("userId") userId: Int,
        @Query("role") role: String = "builder"
    ): BuilderDashboardDto
}
