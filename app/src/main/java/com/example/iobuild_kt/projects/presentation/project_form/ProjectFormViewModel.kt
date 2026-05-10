package com.example.iobuild_kt.projects.presentation.project_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.usecase.CreateProjectUseCase
import com.example.iobuild_kt.projects.domain.usecase.GetProjectByIdUseCase
import com.example.iobuild_kt.projects.domain.usecase.UpdateProjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectFormState(
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val totalUnits: String = "",
    val occupiedUnits: String = "",
    val status: String = "Planned",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val isEdit: Boolean = false
)

class ProjectFormViewModel(
    private val getProjectById: GetProjectByIdUseCase,
    private val createProject: CreateProjectUseCase,
    private val updateProject: UpdateProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectFormState())
    val state: StateFlow<ProjectFormState> = _state.asStateFlow()

    fun loadIfEdit(projectId: Int?) {
        if (projectId == null || projectId <= 0) return
        _state.value = _state.value.copy(isEdit = true, isLoading = true)
        viewModelScope.launch {
            val result = getProjectById(projectId)
            if (result.isSuccess) {
                val p = result.getOrDefault(Project(name = ""))
                _state.value = _state.value.copy(
                    name = p.name, description = p.description, location = p.location,
                    totalUnits = p.totalUnits.toString(), occupiedUnits = p.occupiedUnits.toString(),
                    status = p.status, imageUrl = p.imageUrl, isLoading = false
                )
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message, isLoading = false)
            }
        }
    }

    fun onNameChange(v: String) { _state.value = _state.value.copy(name = v, error = null) }
    fun onDescriptionChange(v: String) { _state.value = _state.value.copy(description = v, error = null) }
    fun onLocationChange(v: String) { _state.value = _state.value.copy(location = v, error = null) }
    fun onTotalUnitsChange(v: String) { _state.value = _state.value.copy(totalUnits = v, error = null) }
    fun onOccupiedUnitsChange(v: String) { _state.value = _state.value.copy(occupiedUnits = v, error = null) }
    fun onStatusChange(v: String) { _state.value = _state.value.copy(status = v, error = null) }
    fun onImageUrlChange(v: String) { _state.value = _state.value.copy(imageUrl = v, error = null) }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) { _state.value = s.copy(error = "El nombre es obligatorio"); return }

        viewModelScope.launch {
            _state.value = s.copy(isSaving = true, error = null)
            val project = Project(
                name = s.name, description = s.description, location = s.location,
                totalUnits = s.totalUnits.toIntOrNull() ?: 0,
                occupiedUnits = s.occupiedUnits.toIntOrNull() ?: 0,
                status = s.status, imageUrl = s.imageUrl
            )
            val result = if (s.isEdit) updateProject(project) else createProject(project)
            if (result.isSuccess) {
                _state.value = _state.value.copy(isSaving = false, saved = true)
            } else {
                _state.value = _state.value.copy(isSaving = false, error = result.exceptionOrNull()?.message)
            }
        }
    }
}
