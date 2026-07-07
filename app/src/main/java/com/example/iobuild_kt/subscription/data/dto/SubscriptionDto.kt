package com.example.iobuild_kt.subscription.data.dto

data class SubscriptionDto(
    val id: Int,
    val builderId: Int,
    val plan: PlanDto,
    val status: String = "Active",
    val startDate: String? = null,
    val endDate: String? = null
)

data class PaymentSessionDto(
    val sessionId: String,
    val checkoutUrl: String,
    val amountInCents: Long,
    val currency: String,
    val planId: Int,
    val planName: String
)

data class PaymentConfirmationDto(
    val status: String,
    val subscriptionId: Int,
    val isNewSubscription: Boolean
)
