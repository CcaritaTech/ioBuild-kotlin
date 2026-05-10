package com.example.iobuild_kt.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.profile.domain.model.Profile
import com.example.iobuild_kt.profile.domain.usecase.GetProfileUseCase
import com.example.iobuild_kt.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    // Edit fields
    val editName: String = "", val editUsername: String = "",
    val editPhone: String = "", val editAddress: String = "",
    val editAge: String = ""
)

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init { loadProfile() }

    fun loadProfile(userId: Int = 1) {
        viewModelScope.launch {
            _state.value = ProfileUiState(isLoading = true)
            getProfile(userId).let { result ->
                if (result.isSuccess) {
                    val p = result.getOrDefault(Profile(name = ""))
                    _state.value = ProfileUiState(
                        isLoading = false, profile = p,
                        editName = p.name, editUsername = p.username,
                        editPhone = p.phoneNumber, editAddress = p.address,
                        editAge = p.age.toString()
                    )
                } else {
                    _state.value = ProfileUiState(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun startEditing() {
        val p = _state.value.profile ?: return
        _state.value = _state.value.copy(isEditing = true,
            editName = p.name, editUsername = p.username, editPhone = p.phoneNumber,
            editAddress = p.address, editAge = p.age.toString())
    }

    fun cancelEditing() { _state.value = _state.value.copy(isEditing = false) }

    fun onNameChange(v: String) { _state.value = _state.value.copy(editName = v) }
    fun onUsernameChange(v: String) { _state.value = _state.value.copy(editUsername = v) }
    fun onPhoneChange(v: String) { _state.value = _state.value.copy(editPhone = v) }
    fun onAddressChange(v: String) { _state.value = _state.value.copy(editAddress = v) }
    fun onAgeChange(v: String) { _state.value = _state.value.copy(editAge = v) }

    fun saveProfile() {
        val s = _state.value; val original = s.profile ?: return
        viewModelScope.launch {
            _state.value = s.copy(isSaving = true, error = null)
            updateProfile(original.copy(
                name = s.editName, username = s.editUsername,
                phoneNumber = s.editPhone, address = s.editAddress,
                age = s.editAge.toIntOrNull() ?: original.age
            )).let { result ->
                if (result.isSuccess) {
                    _state.value = _state.value.copy(isSaving = false, isEditing = false, saved = true, profile = result.getOrDefault(original))
                } else {
                    _state.value = _state.value.copy(isSaving = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }
}
