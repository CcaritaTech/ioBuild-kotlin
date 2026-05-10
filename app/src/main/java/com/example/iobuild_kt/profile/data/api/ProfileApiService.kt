package com.example.iobuild_kt.profile.data.api

import com.example.iobuild_kt.profile.data.dto.ProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class UpdateProfileRequest(
    val name: String, val username: String, val address: String = "",
    val age: Int = 0, val phoneNumber: String = "", val photoUrl: String = ""
)

interface ProfileApiService {
    @GET("profiles")
    suspend fun getAllProfiles(): List<ProfileDto>

    @PUT("profiles/{id}")
    suspend fun updateProfile(@Path("id") id: Int, @Body request: UpdateProfileRequest): ProfileDto

    @POST("profiles/second-email")
    suspend fun setSecondEmail(@Query("userId") userId: Int, @Body body: SecondEmailBody)
}

data class SecondEmailBody(val secondEmail: String)
