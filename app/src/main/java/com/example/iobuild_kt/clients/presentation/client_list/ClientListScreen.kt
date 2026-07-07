package com.example.iobuild_kt.clients.presentation.client_list

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.clients.domain.model.Client
import com.example.iobuild_kt.clients.presentation.components.ClientCard
import com.example.iobuild_kt.clients.presentation.components.ClientFormData
import com.example.iobuild_kt.clients.presentation.components.ClientFormDialog
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ClientListScreen(
    viewModel: ClientListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Re-runs on every (re)composition — including when returning to this screen —
    // same reload-on-resume pattern used by ProjectListScreen.
    LaunchedEffect(Unit) { viewModel.loadClients() }

    var showCreate by remember { mutableStateOf(false) }
    var editingClient by remember { mutableStateOf<Client?>(null) }
    var deletingClient by remember { mutableStateOf<Client?>(null) }

    when (val current = state) {
        is ClientListUiState.Loading -> LoadingScreen()
        is ClientListUiState.Error -> ErrorScreen(message = current.message, onRetry = { viewModel.loadClients() })
        is ClientListUiState.Success -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { showCreate = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            ) { padding ->
                if (current.clients.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text(lang("clients.no_results"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(current.clients, key = { it.id }) { client ->
                            ClientCard(
                                client = client,
                                onEdit = { editingClient = client },
                                onDelete = { deletingClient = client }
                            )
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }

            if (showCreate) {
                ClientFormDialog(
                    title = lang("clients.new"), initial = ClientFormData(), projects = current.projects,
                    onDismiss = { showCreate = false },
                    onSave = { viewModel.createClient(it); showCreate = false }
                )
            }

            editingClient?.let { c ->
                ClientFormDialog(
                    title = lang("clients.edit"),
                    initial = ClientFormData(c.fullName, c.projectId, c.projectName, c.accountStatement, c.email, c.phoneNumber, c.address),
                    projects = current.projects,
                    onDismiss = { editingClient = null },
                    onSave = { viewModel.updateClient(c.id, it); editingClient = null }
                )
            }

            deletingClient?.let { c ->
                AlertDialog(
                    onDismissRequest = { deletingClient = null },
                    title = { Text(lang("clients.delete_confirm")) },
                    text = { Text(c.fullName) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.deleteClient(c.id); deletingClient = null }) {
                            Text(lang("general.delete"), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deletingClient = null }) { Text(lang("general.cancel")) }
                    }
                )
            }
        }
    }
}
