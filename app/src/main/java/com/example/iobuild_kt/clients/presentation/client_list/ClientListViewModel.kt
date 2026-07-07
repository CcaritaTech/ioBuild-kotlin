package com.example.iobuild_kt.clients.presentation.client_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.clients.domain.model.Client
import com.example.iobuild_kt.clients.domain.usecase.CreateClientUseCase
import com.example.iobuild_kt.clients.domain.usecase.DeleteClientUseCase
import com.example.iobuild_kt.clients.domain.usecase.GetClientsUseCase
import com.example.iobuild_kt.clients.domain.usecase.UpdateClientUseCase
import com.example.iobuild_kt.clients.presentation.components.ClientFormData
import com.example.iobuild_kt.projects.domain.model.Project
import com.example.iobuild_kt.projects.domain.usecase.GetProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ClientListUiState {
    data object Loading : ClientListUiState()
    data class Success(val clients: List<Client>, val projects: List<Project>) : ClientListUiState()
    data class Error(val message: String) : ClientListUiState()
}

class ClientListViewModel(
    private val getClients: GetClientsUseCase,
    private val createClientUseCase: CreateClientUseCase,
    private val updateClientUseCase: UpdateClientUseCase,
    private val deleteClientUseCase: DeleteClientUseCase,
    private val getProjects: GetProjectsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ClientListUiState>(ClientListUiState.Loading)
    val state: StateFlow<ClientListUiState> = _state.asStateFlow()

    // Not filtered by builder: projects created through the app never get a real builderId
    // from the backend (always 0), so scoping clients/dropdown-projects by ownership would
    // hide clients tied to projects the user just created themselves.
    fun loadClients() {
        viewModelScope.launch {
            _state.value = ClientListUiState.Loading
            val clientsResult = getClients()
            val projects = getProjects().getOrDefault(emptyList())
            _state.value = if (clientsResult.isSuccess) {
                ClientListUiState.Success(clientsResult.getOrDefault(emptyList()), projects)
            } else {
                ClientListUiState.Error(clientsResult.exceptionOrNull()?.message ?: "Error al cargar clientes")
            }
        }
    }

    fun createClient(data: ClientFormData) {
        viewModelScope.launch {
            createClientUseCase(Client(
                fullName = data.fullName, projectId = data.projectId, projectName = data.projectName,
                accountStatement = data.accountStatement, email = data.email,
                phoneNumber = data.phoneNumber, address = data.address
            ))
            loadClients()
        }
    }

    fun updateClient(id: Int, data: ClientFormData) {
        viewModelScope.launch {
            updateClientUseCase(Client(
                id = id, fullName = data.fullName, projectId = data.projectId, projectName = data.projectName,
                accountStatement = data.accountStatement, email = data.email,
                phoneNumber = data.phoneNumber, address = data.address
            ))
            loadClients()
        }
    }

    // Renamed the injected use case to deleteClientUseCase — it used to share the name
    // "deleteClient" with this function, and `deleteClient(id)` below resolved to a recursive
    // call to this same function (not the use case's invoke), spawning an unbounded flood of
    // coroutines that never actually called the API and crashed the app with an OOM.
    fun deleteClient(id: Int) {
        viewModelScope.launch { deleteClientUseCase(id); loadClients() }
    }
}
