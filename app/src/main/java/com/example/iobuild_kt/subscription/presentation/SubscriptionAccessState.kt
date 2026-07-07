package com.example.iobuild_kt.subscription.presentation

import com.example.iobuild_kt.subscription.domain.usecase.GetCurrentSubscriptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubscriptionAccessState(
    private val getCurrentSubscription: GetCurrentSubscriptionUseCase
) {
    private val _hasActiveSubscription = MutableStateFlow<Boolean?>(null)
    val hasActiveSubscription: StateFlow<Boolean?> = _hasActiveSubscription.asStateFlow()

    suspend fun refresh(builderId: Int) {
        getCurrentSubscription(builderId).onSuccess { subscription ->
            _hasActiveSubscription.value = subscription != null &&
                subscription.status.equals("Active", ignoreCase = true)
        }
        // On failure, leave the previous value untouched (fail-open on transient errors).
    }
}
