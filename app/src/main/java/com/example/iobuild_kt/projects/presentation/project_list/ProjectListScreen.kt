package com.example.iobuild_kt.projects.presentation.project_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import com.example.iobuild_kt.projects.presentation.components.ProjectGridCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProjectListScreen(
    onProjectClick: (Int) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: ProjectListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val current = state) {
        is ProjectListUiState.Loading -> LoadingScreen()
        is ProjectListUiState.Error -> ErrorScreen(
            message = current.message,
            onRetry = { viewModel.loadProjects() }
        )
        is ProjectListUiState.Success -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = onCreateClick) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo proyecto")
                    }
                }
            ) { padding ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(current.projects, key = { it.id }) { project ->
                        ProjectGridCard(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                    }
                }
            }
        }
    }
}
