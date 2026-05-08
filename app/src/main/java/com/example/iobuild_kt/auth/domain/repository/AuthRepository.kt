package com.example.iobuild_kt.auth.domain.repository

import com.example.iobuild_kt.auth.domain.model.AuthenticatedUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AuthenticatedUser>
    suspend fun signOut()
    suspend fun isLoggedIn(): Boolean
    suspend fun getSavedUserId(): Int?
    suspend fun getSavedUserRole(): String?
}
