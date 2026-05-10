package com.example.iobuild_kt.projects.domain.usecase

import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.repository.ProjectRepository

class CreateProjectUseCase(private val repository: ProjectRepository) {
    suspend operator fun invoke(project: Project): Result<Project> = repository.createProject(project)
}
