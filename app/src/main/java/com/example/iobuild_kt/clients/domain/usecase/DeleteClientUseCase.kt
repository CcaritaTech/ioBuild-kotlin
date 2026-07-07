package com.example.iobuild_kt.clients.domain.usecase

import com.example.iobuild_kt.clients.domain.repository.ClientRepository

class DeleteClientUseCase(private val repository: ClientRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deleteClient(id)
}
