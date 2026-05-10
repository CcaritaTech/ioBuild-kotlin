package com.example.iobuild_kt.profile.domain.usecase

import com.example.iobuild_kt.profile.domain.repository.ProfileRepository

class GetProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: Int) = repository.getProfile(userId)
}
