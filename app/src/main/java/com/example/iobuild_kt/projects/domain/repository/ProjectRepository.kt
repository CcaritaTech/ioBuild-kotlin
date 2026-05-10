package com.example.iobuild_kt.projects.domain.repository

import com.example.iobuild_kt.projects.domain.model.Project

interface ProjectRepository {
    suspend fun getProjects(): Result<List<Project>>
    suspend fun getProjectById(id: Int): Result<Project>
    suspend fun createProject(project: Project): Result<Project>
    suspend fun updateProject(project: Project): Result<Project>
    suspend fun deleteProject(id: Int): Result<Unit>
}
