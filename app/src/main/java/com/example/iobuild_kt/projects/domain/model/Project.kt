package com.example.iobuild_kt.projects.domain.model

data class Project(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val location: String = "",
    val totalUnits: Int = 0,
    val occupiedUnits: Int = 0,
    val status: String = "Planned",
    val builderId: Int = 1,
    val createdDate: String = "",
    val imageUrl: String = ""
) {
    val occupancyRate: Double
        get() = if (totalUnits > 0) (occupiedUnits.toDouble() / totalUnits) * 100.0 else 0.0
}
