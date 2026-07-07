package com.example.iobuild_kt.clients.data.api

import com.example.iobuild_kt.clients.data.dto.ClientDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CreateClientRequest(
    val fullName: String,
    val projectId: Int = 0,
    val projectName: String = "",
    val accountStatement: String = "Active",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

data class UpdateClientRequest(
    val fullName: String,
    val projectId: Int = 0,
    val projectName: String = "",
    val accountStatement: String = "Active",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

interface ClientApiService {
    @GET("clients")
    suspend fun getAllClients(): List<ClientDto>

    @GET("clients/{id}")
    suspend fun getClientById(@Path("id") id: Int): ClientDto

    @POST("clients")
    suspend fun createClient(@Body request: CreateClientRequest): ClientDto

    @PUT("clients/{id}")
    suspend fun updateClient(@Path("id") id: Int, @Body request: UpdateClientRequest): ClientDto

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") id: Int)
}
