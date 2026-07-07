package com.example.iobuild_kt.clients.domain.repository

import com.example.iobuild_kt.clients.domain.model.Client

interface ClientRepository {
    suspend fun getClients(): Result<List<Client>>
    suspend fun getClientById(id: Int): Result<Client>
    suspend fun createClient(client: Client): Result<Client>
    suspend fun updateClient(client: Client): Result<Client>
    suspend fun deleteClient(id: Int): Result<Unit>
}
