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
    private val deleteProject: DeleteProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProjectListUiState>(ProjectListUiState.Loading)
    val state: StateFlow<ProjectListUiState> = _state.asStateFlow()

    init { loadProjects() }

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

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            deleteProject(id)
            loadProjects()
        }
    }
}
