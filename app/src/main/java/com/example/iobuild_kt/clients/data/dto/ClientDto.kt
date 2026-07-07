package com.example.iobuild_kt.clients.data.dto

data class ClientDto(
    val id: Int,
    val fullName: String,
    val projectId: Int = 0,
    val projectName: String = "",
    val accountStatement: String = "Active",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)
