package com.example.iobuild_kt.projects.presentation.project_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.usecase.DeleteProjectUseCase
import com.example.iobuild_kt.projects.domain.usecase.GetProjectByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProjectDetailUiState {
    data object Loading : ProjectDetailUiState()
    data class Success(val project: Project) : ProjectDetailUiState()
    data class Error(val message: String) : ProjectDetailUiState()
}

class ProjectDetailViewModel(
    private val getProjectById: GetProjectByIdUseCase,
    private val deleteUseCase: DeleteProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val state: StateFlow<ProjectDetailUiState> = _state.asStateFlow()

    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult.asStateFlow()

    fun loadProject(id: Int) {
        viewModelScope.launch {
            _state.value = ProjectDetailUiState.Loading
            val result = getProjectById(id)
            if (result.isSuccess) {
                _state.value = ProjectDetailUiState.Success(result.getOrDefault(Project(name = "")))
            } else {
                _state.value = ProjectDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error"
                )
            }
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            val result = deleteUseCase(id)
            _deleteResult.value = result.isSuccess
        }
    }
}
