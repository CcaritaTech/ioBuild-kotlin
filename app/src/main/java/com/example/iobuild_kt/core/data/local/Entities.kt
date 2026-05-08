package com.example.iobuild_kt.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val location: String,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val status: String,
    val builderId: Int,
    val createdDate: String,
    val imageUrl: String
)

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: Int,
    val fullName: String,
    val projectId: Int,
    val projectName: String,
    val accountStatement: String,
    val email: String,
    val phoneNumber: String,
    val address: String
)

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val location: String,
    val macAddress: String,
    val projectId: Int,
    val status: String
)

@Entity(tableName = "cache_metadata")
data class CacheMetadata(
    @PrimaryKey val tableName: String,
    val lastFetchedAt: Long
)
