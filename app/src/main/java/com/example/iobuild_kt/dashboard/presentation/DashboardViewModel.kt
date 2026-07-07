package com.example.iobuild_kt.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.dashboard.domain.model.BuilderDashboard
import com.example.iobuild_kt.dashboard.domain.usecase.GetBuilderDashboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    data object Loading : DashboardUiState()
    data class Success(val dashboard: BuilderDashboard) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(
    private val getBuilderDashboard: GetBuilderDashboardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    // Not scoped to the logged-in user: the backend's analytics endpoint only has real seed
    // data under builder id 1, and every project/device created through the app never gets a
    // real builderId anyway (backend bug, always 0), so per-user metrics don't exist to show.
    // Always showing builder 1's demo data keeps the dashboard populated for the demo.
    private val demoBuilderId = 1

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = DashboardUiState.Loading
            val result = getBuilderDashboard(demoBuilderId)
            _state.value = result.fold(
                onSuccess = { DashboardUiState.Success(it) },
                onFailure = { DashboardUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }
}
