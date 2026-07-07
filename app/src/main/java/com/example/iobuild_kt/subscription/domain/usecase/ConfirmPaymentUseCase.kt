package com.example.iobuild_kt.subscription.domain.usecase

import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository

class ConfirmPaymentUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(builderId: Int, sessionId: String) =
        repository.confirmPayment(builderId, sessionId)
}
