package com.example.iobuild_kt.subscription.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.core.data.TokenManager
import com.example.iobuild_kt.core.payment.PaymentReturnBus
import com.example.iobuild_kt.subscription.domain.model.Plan
import com.example.iobuild_kt.subscription.domain.model.Subscription
import com.example.iobuild_kt.subscription.domain.usecase.ConfirmPaymentUseCase
import com.example.iobuild_kt.subscription.domain.usecase.CreateCheckoutSessionUseCase
import com.example.iobuild_kt.subscription.domain.usecase.GetCurrentSubscriptionUseCase
import com.example.iobuild_kt.subscription.domain.usecase.GetPlansUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SubscriptionUiState(
    val isLoading: Boolean = true,
    val plans: List<Plan> = emptyList(),
    val currentSubscription: Subscription? = null,
    val isProcessingPayment: Boolean = false,
    val checkoutUrl: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class SubscriptionViewModel(
    private val getPlans: GetPlansUseCase,
    private val getCurrentSubscription: GetCurrentSubscriptionUseCase,
    private val createCheckoutSession: CreateCheckoutSessionUseCase,
    private val confirmPayment: ConfirmPaymentUseCase,
    private val tokenManager: TokenManager,
    private val paymentReturnBus: PaymentReturnBus,
    private val subscriptionAccessState: SubscriptionAccessState
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionUiState())
    val state: StateFlow<SubscriptionUiState> = _state.asStateFlow()

    private var builderId: Int? = null

    init {
        loadData()
        viewModelScope.launch {
            paymentReturnBus.events.collect { event ->
                when {
                    event.success && event.sessionId != null -> confirm(event.sessionId)
                    else -> _state.value = _state.value.copy(isProcessingPayment = false)
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val id = tokenManager.userId.first()
            builderId = id
            if (id == null) {
                _state.value = _state.value.copy(isLoading = false, error = "No hay sesión activa")
                return@launch
            }
            val plansResult = getPlans()
            val subscriptionResult = getCurrentSubscription(id)
            _state.value = _state.value.copy(
                isLoading = false,
                plans = plansResult.getOrDefault(emptyList()),
                currentSubscription = subscriptionResult.getOrNull(),
                error = plansResult.exceptionOrNull()?.message
            )
        }
    }

    fun subscribe(planId: Int) {
        val id = builderId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessingPayment = true, error = null, successMessage = null)
            val result = createCheckoutSession(id, planId)
            _state.value = if (result.isSuccess) {
                _state.value.copy(checkoutUrl = result.getOrThrow().checkoutUrl)
            } else {
                _state.value.copy(isProcessingPayment = false, error = result.exceptionOrNull()?.message)
            }
        }
    }

    /** Called by the screen right after it launches the Custom Tab, to avoid relaunching on recomposition. */
    fun onCheckoutLaunched() {
        _state.value = _state.value.copy(checkoutUrl = null)
    }

    private fun confirm(sessionId: String) {
        val id = builderId ?: return
        viewModelScope.launch {
            val result = confirmPayment(id, sessionId)
            _state.value = if (result.isSuccess) {
                _state.value.copy(isProcessingPayment = false, successMessage = result.getOrThrow().status)
            } else {
                _state.value.copy(isProcessingPayment = false, error = result.exceptionOrNull()?.message)
            }
            loadData()
            subscriptionAccessState.refresh(id)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}
