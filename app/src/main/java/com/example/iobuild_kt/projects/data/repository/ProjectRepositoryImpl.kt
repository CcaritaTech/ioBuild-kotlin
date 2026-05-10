package com.example.iobuild_kt.projects.data.repository

import com.example.iobuild_kt.core.data.local.MetadataDao
import com.example.iobuild_kt.core.data.local.ProjectDao
import com.example.iobuild_kt.core.data.local.ProjectEntity
import com.example.iobuild_kt.core.data.local.CacheMetadata
import com.example.iobuild_kt.projects.data.api.ProjectApiService
import com.example.iobuild_kt.projects.data.dto.toCreateRequest
import com.example.iobuild_kt.projects.data.dto.toDomain
import com.example.iobuild_kt.projects.data.dto.toUpdateRequest
import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val api: ProjectApiService,
    private val dao: ProjectDao,
    private val meta: MetadataDao
) : ProjectRepository {

    companion object { private const val TTL = 300_000L }

    override suspend fun getProjects(): Result<List<Project>> {
        val cached = dao.getAll()
        val isExpired = meta.get("projects")?.let {
            System.currentTimeMillis() - it.lastFetchedAt > TTL
        } ?: true

        if (cached.isNotEmpty() && !isExpired) {
            return Result.success(cached.map { it.toDomain() })
        }

        return try {
            val fresh = api.getAllProjects()
            dao.deleteAll()
            dao.insertAll(fresh.map { it.toEntity() })
            meta.upsert(CacheMetadata("projects", System.currentTimeMillis()))
            Result.success(fresh.map { it.toDomain() })
        } catch (e: Exception) {
            if (cached.isNotEmpty()) Result.success(cached.map { it.toDomain() })
            else Result.failure(e)
        }
    }

    override suspend fun getProjectById(id: Int): Result<Project> {
        val cached = dao.getById(id)
        return if (cached != null) Result.success(cached.toDomain())
        else runCatching { api.getProjectById(id).also { dao.insert(it.toEntity()) }.toDomain() }
    }

    override suspend fun createProject(project: Project): Result<Project> = runCatching {
        val dto = api.createProject(project.toCreateRequest())
        dao.insert(dto.toEntity())    // write-through
        meta.upsert(CacheMetadata("projects", System.currentTimeMillis()))
        dto.toDomain()
    }

    override suspend fun updateProject(project: Project): Result<Project> = runCatching {
        val dto = api.updateProject(project.id, project.toUpdateRequest())
        dao.insert(dto.toEntity())    // write-through
        dto.toDomain()
    }

    override suspend fun deleteProject(id: Int): Result<Unit> = runCatching {
        api.deleteProject(id)
        dao.deleteById(id)            // write-through
    }
}

// Mappers — extension functions
private fun ProjectEntity.toDomain() = Project(
    id = id, name = name, description = description, location = location,
    totalUnits = totalUnits, occupiedUnits = occupiedUnits, status = status,
    builderId = builderId, createdDate = createdDate, imageUrl = imageUrl
)

private fun com.example.iobuild_kt.projects.data.dto.ProjectDto.toEntity() = ProjectEntity(
    id = id, name = name, description = description, location = location,
    totalUnits = totalUnits, occupiedUnits = occupiedUnits, status = status,
    builderId = builderId, createdDate = createdDate, imageUrl = imageUrl
)
