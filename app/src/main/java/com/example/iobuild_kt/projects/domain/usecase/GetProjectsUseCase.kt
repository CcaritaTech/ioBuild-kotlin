package com.example.iobuild_kt.projects.domain.usecase

import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.repository.ProjectRepository

class GetProjectsUseCase(private val repository: ProjectRepository) {
    suspend operator fun invoke(): Result<List<Project>> = repository.getProjects()
}
