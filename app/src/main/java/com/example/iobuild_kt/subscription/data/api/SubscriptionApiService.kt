package com.example.iobuild_kt.subscription.data.api

import com.example.iobuild_kt.subscription.data.dto.PaymentConfirmationDto
import com.example.iobuild_kt.subscription.data.dto.PaymentSessionDto
import com.example.iobuild_kt.subscription.data.dto.PlanDto
import com.example.iobuild_kt.subscription.data.dto.SubscriptionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class CreateCheckoutSessionRequest(
    val builderId: Int,
    val planId: Int,
    val successUrl: String,
    val cancelUrl: String
)

data class ConfirmPaymentRequest(
    val builderId: Int,
    val sessionId: String
)

interface SubscriptionApiService {
    @GET("plans")
    suspend fun getPlans(): List<PlanDto>

    @GET("subscriptions")
    suspend fun getSubscriptions(): List<SubscriptionDto>

    @POST("subscriptions/payments/create-session")
    suspend fun createCheckoutSession(@Body request: CreateCheckoutSessionRequest): PaymentSessionDto

    @POST("subscriptions/payments/confirm")
    suspend fun confirmPayment(@Body request: ConfirmPaymentRequest): PaymentConfirmationDto
}
