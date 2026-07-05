package com.example.iobuild_kt.auth.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.i18n.lang
import org.koin.androidx.compose.koinViewModel

/**
 * [viewModel] must always be the `"register"` nav-graph-scoped instance passed explicitly by
 * `NavGraph.kt`; the `koinViewModel()` default only exists to satisfy the standard call
 * convention used by every screen in this codebase and must never be relied upon here.
 */
@Composable
fun RegisterProfileScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    LaunchedEffect(state.step) {
        if (state.step == RegisterStep.ACCOUNT) {
            onBack()
        }
    }

    // Intercept the system back gesture/button so it goes through the same ViewModel state
    // transition as the on-screen "Atrás" button. Without this, popping the nav stack directly
    // leaves the ViewModel's step stuck on PROFILE, which makes RegisterAccountScreen's
    // LaunchedEffect(state.step) immediately call onNext() again when it re-composes.
    BackHandler {
        viewModel.backToAccountStep()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = lang("register.profile_title"),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lang("register.profile_subtitle"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text(lang("register.name")) },
                placeholder = { Text(lang("register.name_placeholder")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChanged,
                label = { Text(lang("register.username")) },
                placeholder = { Text(lang("register.username_placeholder")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChanged,
                label = { Text(lang("register.address")) },
                placeholder = { Text(lang("register.address_placeholder")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.age,
                onValueChange = viewModel::onAgeChanged,
                label = { Text(lang("register.age")) },
                placeholder = { Text(lang("register.age_placeholder")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                label = { Text(lang("register.phone_number")) },
                placeholder = { Text(lang("register.phone_number_placeholder")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = viewModel::backToAccountStep,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(lang("register.back"))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::submitProfileStep,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = lang("register.submit"),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
