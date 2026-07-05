package com.example.iobuild_kt.profile.data.repository

import com.example.iobuild_kt.profile.data.api.CreateProfileRequest
import com.example.iobuild_kt.profile.data.api.ProfileApiService
import com.example.iobuild_kt.profile.data.api.SecondEmailBody
import com.example.iobuild_kt.profile.data.api.UpdateProfileRequest
import com.example.iobuild_kt.profile.data.dto.ProfileDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeProfileApiService(
    private val createResponse: ProfileDto? = null
) : ProfileApiService {
    var lastCreateRequest: CreateProfileRequest? = null

    override suspend fun getAllProfiles(): List<ProfileDto> = emptyList()

    override suspend fun updateProfile(id: Int, request: UpdateProfileRequest): ProfileDto =
        error("not used in this test")

    override suspend fun createProfile(request: CreateProfileRequest): ProfileDto {
        lastCreateRequest = request
        return createResponse ?: error("no createResponse configured")
    }

    override suspend fun setSecondEmail(userId: Int, body: SecondEmailBody) =
        error("not used in this test")
}

class ProfileRepositoryImplCreateProfileTest {

    @Test
    fun `createProfile sends the given fields and returns the created profile`() = runTest {
        val created = ProfileDto(5, 42, "", "Ana Torres", "ana", "Av. Siempre Viva", 30, "999999999", null)
        val fakeApi = FakeProfileApiService(createResponse = created)
        val repo = ProfileRepositoryImpl(fakeApi)

        val result = repo.createProfile(
            userId = 42,
            photoUrl = "",
            name = "Ana Torres",
            username = "ana",
            address = "Av. Siempre Viva",
            age = 30,
            phoneNumber = "999999999"
        )

        assertTrue(result.isSuccess)
        assertEquals(42, fakeApi.lastCreateRequest?.userId)
        assertEquals("ana", fakeApi.lastCreateRequest?.username)
        assertEquals(5, result.getOrNull()?.id)
    }
}
