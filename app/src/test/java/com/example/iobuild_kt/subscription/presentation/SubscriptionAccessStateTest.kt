package com.example.iobuild_kt.subscription.presentation

import com.example.iobuild_kt.subscription.domain.model.CheckoutSession
import com.example.iobuild_kt.subscription.domain.model.PaymentConfirmation
import com.example.iobuild_kt.subscription.domain.model.Plan
import com.example.iobuild_kt.subscription.domain.model.Subscription
import com.example.iobuild_kt.subscription.domain.repository.SubscriptionRepository
import com.example.iobuild_kt.subscription.domain.usecase.GetCurrentSubscriptionUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private class FakeSubscriptionRepository(
    var currentSubscriptionResult: Result<Subscription?> = Result.success(null)
) : SubscriptionRepository {
    override suspend fun getPlans(): Result<List<Plan>> = error("not used in this test")
    override suspend fun getCurrentSubscription(builderId: Int): Result<Subscription?> = currentSubscriptionResult
    override suspend fun createCheckoutSession(builderId: Int, planId: Int) = error("not used in this test")
    override suspend fun confirmPayment(builderId: Int, sessionId: String) = error("not used in this test")
}

private fun activeSubscription(status: String = "Active") = Subscription(
    id = 1, builderId = 42,
    plan = Plan(1, "Pro", 999.0, "desc", emptyList(), 10, 5, "email", true, true),
    status = status
)

class SubscriptionAccessStateTest {

    @Test
    fun `starts unresolved before any refresh`() {
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(FakeSubscriptionRepository()))

        assertNull(state.hasActiveSubscription.value)
    }

    @Test
    fun `refresh sets true when the builder has a subscription with status Active`() = runTest {
        val repo = FakeSubscriptionRepository(currentSubscriptionResult = Result.success(activeSubscription()))
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(repo))

        state.refresh(42)

        assertEquals(true, state.hasActiveSubscription.value)
    }

    @Test
    fun `refresh matches Active status case-insensitively`() = runTest {
        val repo = FakeSubscriptionRepository(currentSubscriptionResult = Result.success(activeSubscription(status = "active")))
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(repo))

        state.refresh(42)

        assertEquals(true, state.hasActiveSubscription.value)
    }

    @Test
    fun `refresh sets false when the subscription status is not Active`() = runTest {
        val repo = FakeSubscriptionRepository(currentSubscriptionResult = Result.success(activeSubscription(status = "Cancelled")))
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(repo))

        state.refresh(42)

        assertEquals(false, state.hasActiveSubscription.value)
    }

    @Test
    fun `refresh sets false when the builder has no subscription`() = runTest {
        val repo = FakeSubscriptionRepository(currentSubscriptionResult = Result.success(null))
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(repo))

        state.refresh(42)

        assertEquals(false, state.hasActiveSubscription.value)
    }

    @Test
    fun `a failed refresh keeps the previous value instead of resetting it`() = runTest {
        val repo = FakeSubscriptionRepository(currentSubscriptionResult = Result.success(activeSubscription()))
        val state = SubscriptionAccessState(GetCurrentSubscriptionUseCase(repo))
        state.refresh(42)
        assertEquals(true, state.hasActiveSubscription.value)

        repo.currentSubscriptionResult = Result.failure(Throwable("network error"))
        state.refresh(42)

        assertEquals(true, state.hasActiveSubscription.value)
    }
}
