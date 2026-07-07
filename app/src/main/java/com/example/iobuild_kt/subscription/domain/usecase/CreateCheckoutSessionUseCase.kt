package com.example.iobuild_kt.subscription.domain.usecase

import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository

class CreateCheckoutSessionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(builderId: Int, planId: Int) =
        repository.createCheckoutSession(builderId, planId)
}
