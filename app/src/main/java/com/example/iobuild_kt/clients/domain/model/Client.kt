package com.example.iobuild_kt.clients.domain.model

data class Client(
    val id: Int = 0,
    val fullName: String,
    val projectId: Int = 0,
    val projectName: String = "",
    val accountStatement: String = "Active",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)
