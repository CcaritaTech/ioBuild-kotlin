package com.example.iobuild_kt.projects.presentation.project_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.usecase.DeleteProjectUseCase
import com.example.iobuild_kt.projects.domain.usecase.GetProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProjectListUiState {
    data object Loading : ProjectListUiState()
    data class Success(val projects: List<Project>) : ProjectListUiState()
    data class Error(val message: String) : ProjectListUiState()
}

class ProjectListViewModel(
    private val getProjects: GetProjectsUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProjectListUiState>(ProjectListUiState.Loading)
    val state: StateFlow<ProjectListUiState> = _state.asStateFlow()

    // Loaded from ProjectListScreen's LaunchedEffect(Unit) instead of init{} — the ViewModel
    // survives navigating to ProjectForm and back (same nav back-stack entry), so init{} alone
    // would never re-run and the list would go stale after creating/editing a project.

    // Not filtered by builderId: the backend's CreateProject endpoint never persists the
    // authenticated user's builderId (always stores 0), so filtering here would hide every
    // project a user creates from their own list. Showing everyone's projects is the lesser
    // problem until the backend is fixed.
    fun loadProjects() {
        viewModelScope.launch {
            _state.value = ProjectListUiState.Loading
            val result = getProjects()
            if (result.isSuccess) {
                _state.value = ProjectListUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _state.value = ProjectListUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar proyectos"
                )
            }
        }
    }

    // Renamed the injected use case to deleteProjectUseCase — it used to share the name
    // "deleteProject" with this function, and `deleteProject(id)` below resolved to a recursive
    // call to this same function (not the use case's invoke), spawning an unbounded flood of
    // coroutines that never actually called the API and crashed the app with an OOM.
    fun deleteProject(id: Int) {
        viewModelScope.launch {
            deleteProjectUseCase(id)
            loadProjects()
        }
    }
}
