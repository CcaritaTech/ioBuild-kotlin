package com.example.iobuild_kt.devices.presentation.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.devices.domain.model.Device
import com.example.iobuild_kt.devices.domain.usecase.CreateDeviceUseCase
import com.example.iobuild_kt.devices.domain.usecase.DeleteDeviceUseCase
import com.example.iobuild_kt.devices.domain.usecase.GetDevicesUseCase
import com.example.iobuild_kt.devices.domain.usecase.UpdateDeviceUseCase
import com.example.iobuild_kt.devices.presentation.components.DeviceFormData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeviceListUiState {
    data object Loading : DeviceListUiState()
    data class Success(val devices: List<Device>) : DeviceListUiState()
    data class Error(val message: String) : DeviceListUiState()
}

class DeviceListViewModel(
    private val getDevices: GetDevicesUseCase,
    private val createDeviceUseCase: CreateDeviceUseCase,
    private val updateDeviceUseCase: UpdateDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DeviceListUiState>(DeviceListUiState.Loading)
    val state: StateFlow<DeviceListUiState> = _state.asStateFlow()

    init { loadDevices() }

    // Not filtered by builder: devices only carry a projectId, and projects created through
    // the app never get a real builderId from the backend (always 0), so scoping by owned
    // projects would hide devices on projects the user just created themselves.
    fun loadDevices() {
        viewModelScope.launch {
            _state.value = DeviceListUiState.Loading
            getDevices().let { result ->
                _state.value = if (result.isSuccess) {
                    DeviceListUiState.Success(result.getOrDefault(emptyList()))
                } else {
                    DeviceListUiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar dispositivos")
                }
            }
        }
    }

    fun createDevice(data: DeviceFormData) {
        viewModelScope.launch {
            createDeviceUseCase(Device(
                name = data.name, type = data.type, location = data.location,
                macAddress = data.macAddress, status = data.status
            ))
            loadDevices()
        }
    }

    fun updateDevice(id: Int, data: DeviceFormData) {
        viewModelScope.launch {
            updateDeviceUseCase(Device(
                id = id, name = data.name, type = data.type, location = data.location,
                macAddress = data.macAddress, status = data.status
            ))
            loadDevices()
        }
    }

    // Renamed the injected use case to deleteDeviceUseCase — it used to share the name
    // "deleteDevice" with this function, and `deleteDevice(id)` below resolved to a recursive
    // call to this same function (not the use case's invoke), spawning an unbounded flood of
    // coroutines that never actually called the API and crashed the app with an OOM.
    fun deleteDevice(id: Int) {
        viewModelScope.launch { deleteDeviceUseCase(id); loadDevices() }
    }
}
