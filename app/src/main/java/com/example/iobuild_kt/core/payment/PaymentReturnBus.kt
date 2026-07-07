package com.example.iobuild_kt.core.payment

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

data class PaymentReturnEvent(val success: Boolean, val sessionId: String?)

/**
 * Bridges the Stripe Checkout Custom Tab's deep-link return (captured in MainActivity)
 * to SubscriptionViewModel, without threading the intent through NavGraph route args.
 */
class PaymentReturnBus {
    private val _events = MutableSharedFlow<PaymentReturnEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PaymentReturnEvent> = _events

    fun emit(event: PaymentReturnEvent) {
        _events.tryEmit(event)
    }
}
