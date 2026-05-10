package com.example.iobuild_kt.profile.domain.usecase

import com.example.iobuild_kt.profile.domain.model.Profile
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) = repository.updateProfile(profile)
}
