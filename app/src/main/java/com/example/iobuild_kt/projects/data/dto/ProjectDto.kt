package com.example.iobuild_kt.projects.data.dto

data class ProjectDto(
    val id: Int,
    val name: String,
    val description: String = "",
    val location: String = "",
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val status: String = "Planned",
    val builderId: Int = 1,
    val createdDate: String = "",
    val imageUrl: String = ""
)
