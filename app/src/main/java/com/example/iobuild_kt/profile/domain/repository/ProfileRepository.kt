package com.example.iobuild_kt.profile.domain.repository

import com.example.iobuild_kt.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(userId: Int): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Profile>
}
