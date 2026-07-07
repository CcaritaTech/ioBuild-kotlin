package com.example.iobuild_kt.subscription.domain.model

data class Subscription(
    val id: Int = 0,
    val builderId: Int,
    val plan: Plan,
    val status: String = "Active",
    val startDate: String? = null,
    val endDate: String? = null
)

data class CheckoutSession(
    val sessionId: String,
    val checkoutUrl: String,
    val amountInCents: Long,
    val currency: String,
    val planId: Int,
    val planName: String
)

data class PaymentConfirmation(
    val status: String,
    val subscriptionId: Int,
    val isNewSubscription: Boolean
)
