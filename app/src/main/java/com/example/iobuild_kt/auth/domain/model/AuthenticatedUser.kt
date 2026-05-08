package com.example.iobuild_kt.auth.domain.model

data class AuthenticatedUser(
    val id: Int,
    val email: String,
    val role: String,
    val token: String
)
