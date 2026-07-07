package com.example.iobuild_kt.subscription.domain.usecase

import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository

class GetPlansUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke() = repository.getPlans()
}
