package com.example.iobuild_kt.clients.domain.usecase

import com.example.iobuild_kt.clients.domain.model.Client
import com.example.iobuild_kt.clients.domain.repository.ClientRepository

class CreateClientUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(client: Client) = repository.createClient(client)
}
