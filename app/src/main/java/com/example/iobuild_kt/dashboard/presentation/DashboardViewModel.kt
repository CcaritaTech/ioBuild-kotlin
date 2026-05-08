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

    private var currentUserId: Int = 1 // default builder user

    init {
        loadDashboard()
    }

    fun loadDashboard(userId: Int = currentUserId) {
        currentUserId = userId
        viewModelScope.launch {
            _state.value = DashboardUiState.Loading
            val result = getBuilderDashboard(userId)
            _state.value = result.fold(
                onSuccess = { DashboardUiState.Success(it) },
                onFailure = { DashboardUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }
}
