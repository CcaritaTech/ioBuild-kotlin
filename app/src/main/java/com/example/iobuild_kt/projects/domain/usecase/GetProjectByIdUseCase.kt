package com.example.iobuild_kt.projects.domain.usecase

import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.repository.ProjectRepository

class GetProjectByIdUseCase(private val repository: ProjectRepository) {
    suspend operator fun invoke(id: Int): Result<Project> = repository.getProjectById(id)
}
