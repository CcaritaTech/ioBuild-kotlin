package com.example.iobuild_kt.subscription.presentation

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import com.example.iobuild_kt.subscription.domain.model.Plan
import com.example.iobuild_kt.subscription.domain.model.Subscription
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Launch the Stripe-hosted Checkout page in a Custom Tab as soon as the session is ready.
    LaunchedEffect(state.checkoutUrl) {
        val url = state.checkoutUrl ?: return@LaunchedEffect
        CustomTabsIntent.Builder().build().launchUrl(context, url.toUri())
        viewModel.onCheckoutLaunched()
    }

    when {
        state.isLoading -> LoadingScreen()
        state.error != null && state.plans.isEmpty() -> ErrorScreen(
            message = state.error!!,
            onRetry = { viewModel.loadData() }
        )
        else -> SubscriptionContent(state = state, onSubscribe = { viewModel.subscribe(it) })
    }
}

@Composable
private fun SubscriptionContent(
    state: SubscriptionUiState,
    onSubscribe: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text(lang("subscription.title"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }

        item {
            if (state.isProcessingPayment) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(lang("subscription.processing"), modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            state.successMessage?.let {
                Text(lang("subscription.payment_confirmed"), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            state.error?.let {
                if (state.plans.isNotEmpty()) Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            Text(lang("subscription.current_plan"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            val sub = state.currentSubscription
            if (sub == null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(lang("subscription.no_subscription"), fontWeight = FontWeight.Bold)
                        Text(lang("subscription.no_subscription_desc"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                CurrentSubscriptionCard(sub)
            }
        }

        item {
            Text(lang("subscription.plans"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(state.plans, key = { it.id }) { plan ->
            PlanCard(
                plan = plan,
                isCurrent = state.currentSubscription?.plan?.id == plan.id,
                enabled = !state.isProcessingPayment,
                onSubscribe = { onSubscribe(plan.id) }
            )
        }
    }
}

@Composable
private fun CurrentSubscriptionCard(subscription: Subscription) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(subscription.plan.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${lang("subscription.price")} ${subscription.plan.price} ${lang("subscription.per_month")}")
            Text(
                if (subscription.status.equals("active", ignoreCase = true)) lang("subscription.status_active") else subscription.status,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    isCurrent: Boolean,
    enabled: Boolean,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(plan.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(plan.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "${lang("subscription.price")} ${plan.price} ${lang("subscription.per_month")}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            plan.features.forEach { feature -> Text("• $feature", style = MaterialTheme.typography.bodySmall) }
            Button(
                onClick = onSubscribe,
                enabled = enabled && !isCurrent,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text(if (isCurrent) lang("subscription.plan_current") else lang("subscription.subscribe"))
            }
        }
    }
}
