package com.example.iobuild_kt.devices.data.repository

import com.example.iobuild_kt.core.data.local.CacheMetadata
import com.example.iobuild_kt.core.data.local.DeviceDao
import com.example.iobuild_kt.core.data.local.DeviceEntity
import com.example.iobuild_kt.core.data.local.MetadataDao
import com.example.iobuild_kt.devices.data.api.DeviceApiService
import com.example.iobuild_kt.devices.data.dto.toCreateRequest
import com.example.iobuild_kt.devices.data.dto.toDomain
import com.example.iobuild_kt.devices.data.dto.toUpdateRequest
import com.example.iobuild_kt.devices.domain.model.Device
import com.example.iobuild_kt.devices.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val api: DeviceApiService,
    private val dao: DeviceDao,
    private val meta: MetadataDao
) : DeviceRepository {

    companion object { private const val TTL = 300_000L }

    override suspend fun getDevices(): Result<List<Device>> {
        val cached = dao.getAll()
        val isExpired = meta.get("devices")?.let {
            System.currentTimeMillis() - it.lastFetchedAt > TTL
        } ?: true

        if (cached.isNotEmpty() && !isExpired) return Result.success(cached.map { it.toDomain() })

        return try {
            val fresh = api.getAllDevices()
            dao.deleteAll()
            dao.insertAll(fresh.map { it.toEntity() })
            meta.upsert(CacheMetadata("devices", System.currentTimeMillis()))
            Result.success(fresh.map { it.toDomain() })
        } catch (e: Exception) {
            if (cached.isNotEmpty()) Result.success(cached.map { it.toDomain() })
            else Result.failure(e)
        }
    }

    override suspend fun getDeviceById(id: Int): Result<Device> {
        val cached = dao.getAll().firstOrNull { it.id == id }
        return if (cached != null) Result.success(cached.toDomain())
        else runCatching { api.getDeviceById(id).also { dao.insert(it.toEntity()) }.toDomain() }
    }

    override suspend fun createDevice(device: Device): Result<Device> = runCatching {
        val dto = api.createDevice(device.toCreateRequest())
        dao.insert(dto.toEntity())
        meta.upsert(CacheMetadata("devices", System.currentTimeMillis()))
        dto.toDomain()
    }

    override suspend fun updateDevice(device: Device): Result<Device> = runCatching {
        val dto = api.updateDevice(device.id, device.toUpdateRequest())
        dao.insert(dto.toEntity())
        dto.toDomain()
    }

    override suspend fun deleteDevice(id: Int): Result<Unit> = runCatching {
        api.deleteDevice(id)
        dao.deleteById(id)
    }
}

private fun DeviceEntity.toDomain() = Device(
    id = id, name = name, type = type, location = location,
    macAddress = macAddress, projectId = projectId, status = status
)

private fun com.example.iobuild_kt.devices.data.dto.DeviceDto.toEntity() = DeviceEntity(
    id = id, name = name, type = type, location = location,
    macAddress = macAddress, projectId = projectId, status = status
)
