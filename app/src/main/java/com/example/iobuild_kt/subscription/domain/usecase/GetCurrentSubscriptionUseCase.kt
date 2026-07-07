package com.example.iobuild_kt.subscription.domain.usecase

import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository

class GetCurrentSubscriptionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(builderId: Int) = repository.getCurrentSubscription(builderId)
}
