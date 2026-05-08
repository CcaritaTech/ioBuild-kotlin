package com.example.iobuild_kt.auth.data.api

import retrofit2.http.Body
import retrofit2.http.POST

data class SignInRequest(val email: String, val password: String)

data class SignInResponse(
    val id: Int,
    val email: String,
    val role: String,
    val token: String
)

interface AuthApiService {
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): SignInResponse
}
