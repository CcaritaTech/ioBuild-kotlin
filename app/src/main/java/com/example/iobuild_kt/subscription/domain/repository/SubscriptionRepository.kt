package com.example.iobuild_kt.subscription.domain.repository

import com.example.iobuild_kt.subscription.domain.model.CheckoutSession
import com.example.iobuild_kt.subscription.domain.model.PaymentConfirmation
import com.example.iobuild_kt.subscription.domain.model.Plan
import com.example.iobuild_kt.subscription.domain.model.Subscription

interface SubscriptionRepository {
    suspend fun getPlans(): Result<List<Plan>>
    suspend fun getCurrentSubscription(builderId: Int): Result<Subscription?>
    suspend fun createCheckoutSession(builderId: Int, planId: Int): Result<CheckoutSession>
    suspend fun confirmPayment(builderId: Int, sessionId: String): Result<PaymentConfirmation>
}
