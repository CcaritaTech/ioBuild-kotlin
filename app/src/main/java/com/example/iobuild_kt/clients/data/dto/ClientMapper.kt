package com.example.iobuild_kt.clients.data.dto

import com.example.iobuild_kt.clients.data.api.CreateClientRequest
import com.example.iobuild_kt.clients.data.api.UpdateClientRequest
import com.example.iobuild_kt.clients.domain.model.Client

fun ClientDto.toDomain() = Client(
    id = id,
    fullName = fullName,
    projectId = projectId,
    projectName = projectName,
    accountStatement = accountStatement,
    email = email,
    phoneNumber = phoneNumber,
    address = address
)

fun Client.toCreateRequest() = CreateClientRequest(
    fullName = fullName,
    projectId = projectId,
    projectName = projectName,
    accountStatement = accountStatement,
    email = email,
    phoneNumber = phoneNumber,
    address = address
)

fun Client.toUpdateRequest() = UpdateClientRequest(
    fullName = fullName,
    projectId = projectId,
    projectName = projectName,
    accountStatement = accountStatement,
    email = email,
    phoneNumber = phoneNumber,
    address = address
)
