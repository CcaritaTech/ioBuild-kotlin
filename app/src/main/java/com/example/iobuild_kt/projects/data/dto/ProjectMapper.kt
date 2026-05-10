package com.example.iobuild_kt.projects.data.dto

import com.example.iobuild_kt.projects.data.api.CreateProjectRequest
import com.example.iobuild_kt.projects.data.api.UpdateProjectRequest
import com.example.iobuild_kt.projects.domain.model.Project

fun ProjectDto.toDomain() = Project(
    id = id,
    name = name,
    description = description,
    location = location,
    totalUnits = totalUnits,
    occupiedUnits = occupiedUnits,
    status = status,
    builderId = builderId,
    createdDate = createdDate,
    imageUrl = imageUrl
)

fun Project.toCreateRequest() = CreateProjectRequest(
    name = name,
    description = description,
    location = location,
    totalUnits = totalUnits,
    occupiedUnits = occupiedUnits,
    status = status,
    imageUrl = imageUrl
)

fun Project.toUpdateRequest() = UpdateProjectRequest(
    name = name,
    description = description,
    location = location,
    totalUnits = totalUnits,
    occupiedUnits = occupiedUnits,
    status = status,
    imageUrl = imageUrl
)
