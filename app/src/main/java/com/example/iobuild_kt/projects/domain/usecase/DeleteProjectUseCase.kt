package com.example.iobuild_kt.projects.domain.usecase

import com.example.iobuild_kt.projects.domain.repository.ProjectRepository

class DeleteProjectUseCase(private val repository: ProjectRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deleteProject(id)
}
