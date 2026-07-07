package com.example.iobuild_kt.clients.data.repository

import com.example.iobuild_kt.clients.data.api.ClientApiService
import com.example.iobuild_kt.clients.data.dto.toCreateRequest
import com.example.iobuild_kt.clients.data.dto.toDomain
import com.example.iobuild_kt.clients.data.dto.toUpdateRequest
import com.example.iobuild_kt.clients.domain.model.Client
import com.example.iobuild_kt.clients.domain.repository.ClientRepository
import com.example.iobuild_kt.core.data.local.CacheMetadata
import com.example.iobuild_kt.core.data.local.ClientDao
import com.example.iobuild_kt.core.data.local.ClientEntity
import com.example.iobuild_kt.core.data.local.MetadataDao

class ClientRepositoryImpl(
    private val api: ClientApiService,
    private val dao: ClientDao,
    private val meta: MetadataDao
) : ClientRepository {

    companion object { private const val TTL = 300_000L }

    override suspend fun getClients(): Result<List<Client>> {
        val cached = dao.getAll()
        val isExpired = meta.get("clients")?.let {
            System.currentTimeMillis() - it.lastFetchedAt > TTL
        } ?: true

        if (cached.isNotEmpty() && !isExpired) return Result.success(cached.map { it.toDomain() })

        return try {
            val fresh = api.getAllClients()
            dao.deleteAll()
            dao.insertAll(fresh.map { it.toEntity() })
            meta.upsert(CacheMetadata("clients", System.currentTimeMillis()))
            Result.success(fresh.map { it.toDomain() })
        } catch (e: Exception) {
            if (cached.isNotEmpty()) Result.success(cached.map { it.toDomain() })
            else Result.failure(e)
        }
    }

    override suspend fun getClientById(id: Int): Result<Client> {
        val cached = dao.getAll().firstOrNull { it.id == id }
        return if (cached != null) Result.success(cached.toDomain())
        else runCatching { api.getClientById(id).also { dao.insert(it.toEntity()) }.toDomain() }
    }

    override suspend fun createClient(client: Client): Result<Client> = runCatching {
        val dto = api.createClient(client.toCreateRequest())
        dao.insert(dto.toEntity())
        meta.upsert(CacheMetadata("clients", System.currentTimeMillis()))
        dto.toDomain()
    }

    override suspend fun updateClient(client: Client): Result<Client> = runCatching {
        val dto = api.updateClient(client.id, client.toUpdateRequest())
        dao.insert(dto.toEntity())
        dto.toDomain()
    }

    override suspend fun deleteClient(id: Int): Result<Unit> = runCatching {
        api.deleteClient(id)
        dao.deleteById(id)
    }
}

private fun ClientEntity.toDomain() = Client(
    id = id, fullName = fullName, projectId = projectId, projectName = projectName,
    accountStatement = accountStatement, email = email, phoneNumber = phoneNumber, address = address
)

private fun com.example.iobuild_kt.clients.data.dto.ClientDto.toEntity() = ClientEntity(
    id = id, fullName = fullName, projectId = projectId, projectName = projectName,
    accountStatement = accountStatement, email = email, phoneNumber = phoneNumber, address = address
)
