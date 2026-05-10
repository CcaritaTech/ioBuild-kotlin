package com.example.iobuild_kt.devices.presentation.device_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import com.example.iobuild_kt.devices.domain.model.Device
import com.example.iobuild_kt.devices.presentation.components.DeviceCard
import com.example.iobuild_kt.devices.presentation.components.DeviceFormData
import com.example.iobuild_kt.devices.presentation.components.DeviceFormDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showCreate by remember { mutableStateOf(false) }
    var editingDevice by remember { mutableStateOf<Device?>(null) }
    var deletingDevice by remember { mutableStateOf<Device?>(null) }

    when (val current = state) {
        is DeviceListUiState.Loading -> LoadingScreen()
        is DeviceListUiState.Error -> ErrorScreen(message = current.message, onRetry = { viewModel.loadDevices() })
        is DeviceListUiState.Success -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { showCreate = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            ) { padding ->
                if (current.devices.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text(lang("devices.no_devices"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(current.devices, key = { it.id }) { device ->
                            DeviceCard(
                                device = device,
                                onEdit = { editingDevice = device },
                                onDelete = { deletingDevice = device }
                            )
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }

    // Create dialog
    if (showCreate) {
        DeviceFormDialog(
            title = lang("devices.new"), initial = DeviceFormData(),
            onDismiss = { showCreate = false },
            onSave = { viewModel.createDevice(it); showCreate = false }
        )
    }

    // Edit dialog
    editingDevice?.let { d ->
        DeviceFormDialog(
            title = lang("devices.edit"),
            initial = DeviceFormData(d.name, d.type, d.location, d.macAddress, d.status),
            onDismiss = { editingDevice = null },
            onSave = { viewModel.updateDevice(d.id, it); editingDevice = null }
        )
    }

    // Delete confirmation
    deletingDevice?.let { d ->
        AlertDialog(
            onDismissRequest = { deletingDevice = null },
            title = { Text(lang("devices.delete")) },
            text = { Text("${lang("devices.delete_confirm")} '${d.name}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteDevice(d.id); deletingDevice = null }) {
                    Text(lang("general.delete"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingDevice = null }) { Text(lang("general.cancel")) }
            }
        )
    }
}
