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
    private val createDevice: CreateDeviceUseCase,
    private val updateDevice: UpdateDeviceUseCase,
    private val deleteDevice: DeleteDeviceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DeviceListUiState>(DeviceListUiState.Loading)
    val state: StateFlow<DeviceListUiState> = _state.asStateFlow()

    init { loadDevices() }

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
            createDevice(Device(
                name = data.name, type = data.type, location = data.location,
                macAddress = data.macAddress, status = data.status
            ))
            loadDevices()
        }
    }

    fun updateDevice(id: Int, data: DeviceFormData) {
        viewModelScope.launch {
            updateDevice(Device(
                id = id, name = data.name, type = data.type, location = data.location,
                macAddress = data.macAddress, status = data.status
            ))
            loadDevices()
        }
    }

    fun deleteDevice(id: Int) {
        viewModelScope.launch { deleteDevice(id); loadDevices() }
    }
}
