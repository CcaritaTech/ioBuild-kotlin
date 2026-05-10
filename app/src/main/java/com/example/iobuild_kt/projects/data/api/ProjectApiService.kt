package com.example.iobuild_kt.projects.data.api

import com.example.iobuild_kt.projects.data.dto.ProjectDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateProjectRequest(
    val name: String,
    val description: String = "",
    val location: String = "",
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val status: String = "Planned",
    val imageUrl: String = ""
)

data class UpdateProjectRequest(
    val name: String,
    val description: String = "",
    val location: String = "",
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val status: String = "Planned",
    val imageUrl: String = ""
)

interface ProjectApiService {
    @GET("projects")
    suspend fun getAllProjects(): List<ProjectDto>

    @GET("projects/{id}")
    suspend fun getProjectById(@Path("id") id: Int): ProjectDto

    @POST("projects")
    suspend fun createProject(@Body request: CreateProjectRequest): ProjectDto

    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: Int, @Body request: UpdateProjectRequest): ProjectDto

    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: Int)
}
