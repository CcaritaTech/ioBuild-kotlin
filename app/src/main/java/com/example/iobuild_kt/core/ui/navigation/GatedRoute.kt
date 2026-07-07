package com.example.iobuild_kt.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.iobuild_kt.subscription.presentation.SubscriptionAccessState
import org.koin.compose.koinInject

/**
 * Wraps a gated route's content. Redirects to Subscription once [SubscriptionAccessState]
 * confirms the account has no active subscription. Renders normally while the state is
 * still unresolved (null) or confirmed active (true) — the login/register/biometric flows
 * already resolve this before these routes are reached; this is a defense-in-depth net for
 * direct or deep-link entry.
 */
@Composable
fun GatedRoute(navController: NavHostController, content: @Composable () -> Unit) {
    val access: SubscriptionAccessState = koinInject()
    val hasActive by access.hasActiveSubscription.collectAsState()

    LaunchedEffect(hasActive) {
        if (hasActive == false) {
            navController.navigate(Screen.Subscription.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = false }
            }
        }
    }

    if (hasActive != false) {
        content()
    }
}
