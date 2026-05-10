package com.example.iobuild_kt.profile.data.dto

data class ProfileDto(
    val id: Int, val userId: Int, val photoUrl: String, val name: String,
    val username: String, val address: String, val age: Int,
    val phoneNumber: String, val secondEmail: String? = null
)
