package com.example.iobuild_kt.profile.domain.model

data class Profile(
    val id: Int = 0,
    val userId: Int = 0,
    val photoUrl: String = "",
    val name: String = "",
    val username: String = "",
    val address: String = "",
    val age: Int = 0,
    val phoneNumber: String = "",
    val secondEmail: String? = null
)
