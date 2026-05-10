package com.example.iobuild_kt.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingScreen()
        state.error != null && state.profile == null -> ErrorScreen(message = state.error!!, onRetry = { viewModel.loadProfile() })
        else -> {
            val p = state.profile ?: return
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile photo
                AsyncImage(
                    model = p.photoUrl.ifEmpty { null },
                    contentDescription = null,
                    modifier = Modifier.size(96.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text(p.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(lang("role.builder"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(24.dp))

                if (state.isEditing) {
                    // Edit form
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(value = state.editName, onValueChange = viewModel::onNameChange, label = { Text(lang("profile.name")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = state.editUsername, onValueChange = viewModel::onUsernameChange, label = { Text(lang("profile.username")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = state.editPhone, onValueChange = viewModel::onPhoneChange, label = { Text(lang("profile.phone")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = state.editAddress, onValueChange = viewModel::onAddressChange, label = { Text(lang("profile.address")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = state.editAge, onValueChange = viewModel::onAgeChange, label = { Text(lang("profile.age")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.height(8.dp))
                            if (state.error != null) { Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = viewModel::saveProfile, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
                                if (state.isSaving) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) else Text("Guardar Cambios")
                            }
                            TextButton(onClick = viewModel::cancelEditing) { Text("Cancelar") }
                        }
                    }
                } else {
                    // View mode
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            ProfileField(lang("profile.name"), p.name)
                            ProfileField(lang("profile.username"), p.username)
                            ProfileField(lang("profile.phone"), p.phoneNumber)
                            ProfileField(lang("profile.address"), p.address)
                            ProfileField(lang("profile.age"), if (p.age > 0) "${p.age} ${lang("profile.years")}" else "")
                            if (p.secondEmail != null) ProfileField(lang("profile.second_email"), p.secondEmail!!)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = viewModel::startEditing, modifier = Modifier.fillMaxWidth()) { Text(lang("profile.edit")) }
                }
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    if (value.isNotBlank()) {
        Column(Modifier.padding(vertical = 4.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
