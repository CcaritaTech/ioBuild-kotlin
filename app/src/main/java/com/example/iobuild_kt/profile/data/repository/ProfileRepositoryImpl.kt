package com.example.iobuild_kt.profile.data.repository

import com.example.iobuild_kt.profile.data.api.ProfileApiService
import com.example.iobuild_kt.profile.data.api.SecondEmailBody
import com.example.iobuild_kt.profile.data.dto.toDomain
import com.example.iobuild_kt.profile.data.dto.toUpdateRequest
import com.example.iobuild_kt.profile.domain.model.Profile
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val api: ProfileApiService) : ProfileRepository {

    override suspend fun getProfile(userId: Int): Result<Profile> = runCatching {
        api.getAllProfiles().firstOrNull { it.userId == userId }?.toDomain()
            ?: throw Exception("Perfil no encontrado")
    }

    override suspend fun updateProfile(profile: Profile): Result<Profile> = runCatching {
        api.updateProfile(profile.id, profile.toUpdateRequest()).toDomain()
    }

    suspend fun setSecondEmail(userId: Int, email: String): Result<Unit> = runCatching {
        api.setSecondEmail(userId, SecondEmailBody(email))
    }
}
