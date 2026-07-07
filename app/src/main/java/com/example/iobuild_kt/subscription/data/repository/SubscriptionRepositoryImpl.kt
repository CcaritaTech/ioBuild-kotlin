package com.example.iobuild_kt.subscription.data.repository

import com.example.iobuild_kt.subscription.data.api.ConfirmPaymentRequest
import com.example.iobuild_kt.subscription.data.api.CreateCheckoutSessionRequest
import com.example.iobuild_kt.subscription.data.api.SubscriptionApiService
import com.example.iobuild_kt.subscription.data.dto.toDomain
import com.example.iobuild_kt.subscription.domain.model.CheckoutSession
import com.example.iobuild_kt.subscription.domain.model.PaymentConfirmation
import com.example.iobuild_kt.subscription.domain.model.Plan
import com.example.iobuild_kt.subscription.domain.model.Subscription
import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository

// Deep-link targets registered in AndroidManifest.xml for the Stripe Checkout Custom Tab to return to.
// Stripe substitutes the literal "{CHECKOUT_SESSION_ID}" placeholder itself on redirect.
private const val SUCCESS_URL = "iobuild://payment/success?session_id={CHECKOUT_SESSION_ID}"
private const val CANCEL_URL = "iobuild://payment/cancel"

class SubscriptionRepositoryImpl(
    private val api: SubscriptionApiService
) : SubscriptionRepository {

    override suspend fun getPlans(): Result<List<Plan>> = runCatching {
        api.getPlans().map { it.toDomain() }
    }

    override suspend fun getCurrentSubscription(builderId: Int): Result<Subscription?> = runCatching {
        api.getSubscriptions().map { it.toDomain() }.firstOrNull { it.builderId == builderId }
    }

    override suspend fun createCheckoutSession(builderId: Int, planId: Int): Result<CheckoutSession> = runCatching {
        api.createCheckoutSession(
            CreateCheckoutSessionRequest(builderId, planId, SUCCESS_URL, CANCEL_URL)
        ).toDomain()
    }

    override suspend fun confirmPayment(builderId: Int, sessionId: String): Result<PaymentConfirmation> = runCatching {
        api.confirmPayment(ConfirmPaymentRequest(builderId, sessionId)).toDomain()
    }
}
