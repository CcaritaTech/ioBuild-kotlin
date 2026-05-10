package com.example.iobuild_kt.profile.data.dto

import com.example.iobuild_kt.profile.data.api.UpdateProfileRequest
import com.example.iobuild_kt.profile.domain.model.Profile

fun ProfileDto.toDomain() = Profile(
    id = id, userId = userId, photoUrl = photoUrl, name = name,
    username = username, address = address, age = age,
    phoneNumber = phoneNumber, secondEmail = secondEmail
)

fun Profile.toUpdateRequest() = UpdateProfileRequest(
    name = name, username = username, address = address,
    age = age, phoneNumber = phoneNumber, photoUrl = photoUrl
)
