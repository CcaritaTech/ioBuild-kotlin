package com.example.iobuild_kt.projects.presentation.project_form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.i18n.lang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    projectId: Int?,
    onBack: () -> Unit,
    viewModel: ProjectFormViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) { viewModel.loadIfEdit(projectId) }
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEdit) lang("projects.edit") else lang("projects.new"), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.isLoading) { CircularProgressIndicator(Modifier.padding(32.dp)); return@Column }

            OutlinedTextField(value = state.name, onValueChange = viewModel::onNameChange, label = { Text(lang("projects.name")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.description, onValueChange = viewModel::onDescriptionChange, label = { Text(lang("projects.description")) }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.location, onValueChange = viewModel::onLocationChange, label = { Text(lang("projects.location")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.totalUnits, onValueChange = viewModel::onTotalUnitsChange, label = { Text(lang("projects.total_units")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.occupiedUnits, onValueChange = viewModel::onOccupiedUnitsChange, label = { Text(lang("projects.occupied_units")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.imageUrl, onValueChange = viewModel::onImageUrlChange, label = { Text(lang("projects.image_url")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            if (state.error != null) { Spacer(Modifier.height(12.dp)); Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(24.dp))
            Button(onClick = viewModel::save, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                if (state.isSaving) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text(lang("projects.save"), fontWeight = FontWeight.Bold)
            }
        }
    }
}
