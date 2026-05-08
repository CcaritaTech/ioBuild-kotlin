package com.example.iobuild_kt.auth.data.repository

import com.example.iobuild_kt.auth.data.api.AuthApiService
import com.example.iobuild_kt.auth.data.api.SignInRequest
import com.example.iobuild_kt.auth.domain.model.AuthenticatedUser
import com.example.iobuild_kt.auth.domain.repository.AuthRepository
import com.example.iobuild_kt.core.data.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthenticatedUser> {
        return try {
            val response = api.signIn(SignInRequest(email, password))
            val user = AuthenticatedUser(
                id = response.id,
                email = response.email,
                role = response.role,
                token = response.token
            )
            tokenManager.saveSession(user.id, user.email, user.role, user.token)
            Result.success(user)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                in 400..499 -> "Credenciales incorrectas. Verifica tu email y contraseña."
                else -> "Error del servidor. Intenta de nuevo más tarde."
            }
            Result.failure(Throwable(message))
        } catch (e: IOException) {
            Result.failure(Throwable("Error de conexión. Verifica tu internet."))
        } catch (e: Exception) {
            Result.failure(Throwable("Error inesperado. Intenta de nuevo."))
        }
    }

    override suspend fun signOut() {
        tokenManager.clearSession()
    }

    override suspend fun isLoggedIn(): Boolean {
        return !tokenManager.token.firstOrNull().isNullOrBlank()
    }

    override suspend fun getSavedUserId(): Int? {
        return tokenManager.userId.firstOrNull()
    }

    override suspend fun getSavedUserRole(): String? {
        return tokenManager.userRole.firstOrNull()
    }
}
