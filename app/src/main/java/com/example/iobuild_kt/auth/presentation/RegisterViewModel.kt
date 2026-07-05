package com.example.iobuild_kt.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iobuild_kt.auth.domain.model.AuthenticatedUser
import com.example.iobuild_kt.auth.domain.repository.AuthRepository
import com.example.iobuild_kt.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RegisterStep { ACCOUNT, PROFILE }

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val username: String = "",
    val address: String = "",
    val age: String = "",
    val phoneNumber: String = "",
    val step: RegisterStep = RegisterStep.ACCOUNT,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    private var signedUp = false
    private var authenticatedUser: AuthenticatedUser? = null

    fun onEmailChanged(value: String) {
        _state.value = _state.value.copy(email = value, error = null)
    }

    fun onPasswordChanged(value: String) {
        _state.value = _state.value.copy(password = value, error = null)
    }

    fun onConfirmPasswordChanged(value: String) {
        _state.value = _state.value.copy(confirmPassword = value, error = null)
    }

    fun onNameChanged(value: String) {
        _state.value = _state.value.copy(name = value, error = null)
    }

    fun onUsernameChanged(value: String) {
        _state.value = _state.value.copy(username = value, error = null)
    }

    fun onAddressChanged(value: String) {
        _state.value = _state.value.copy(address = value, error = null)
    }

    fun onAgeChanged(value: String) {
        _state.value = _state.value.copy(age = value, error = null)
    }

    fun onPhoneNumberChanged(value: String) {
        _state.value = _state.value.copy(phoneNumber = value, error = null)
    }

    fun backToAccountStep() {
        // Accepted tradeoff: if sign-up already succeeded but a later step failed, and the user
        // comes back here and resubmits the SAME email unchanged, sign-up is re-sent and hits a
        // 409. Do not remove this reset to "fix" that — without it, re-entering ACCOUNT after a
        // successful sign-up would skip straight back to PROFILE via stale `signedUp` state.
        signedUp = false
        authenticatedUser = null
        _state.value = _state.value.copy(step = RegisterStep.ACCOUNT, error = null)
    }

    fun submitAccountStep() {
        val s = _state.value
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        when {
            s.email.isBlank() || s.password.isBlank() || s.confirmPassword.isBlank() -> {
                _state.value = s.copy(error = "Completa todos los campos")
            }
            !emailRegex.matches(s.email) -> {
                _state.value = s.copy(error = "Ingresa un correo electrónico válido")
            }
            s.password.length < 6 -> {
                _state.value = s.copy(error = "La contraseña debe tener al menos 6 caracteres")
            }
            s.password != s.confirmPassword -> {
                _state.value = s.copy(error = "Las contraseñas no coinciden")
            }
            else -> {
                _state.value = s.copy(step = RegisterStep.PROFILE, error = null)
            }
        }
    }

    fun submitProfileStep() {
        if (_state.value.isLoading) return

        val s = _state.value
        val ageInt = s.age.toIntOrNull()
        when {
            s.name.isBlank() || s.username.isBlank() || s.address.isBlank() || s.phoneNumber.isBlank() -> {
                _state.value = s.copy(error = "Completa todos los campos")
                return
            }
            ageInt == null || ageInt !in 18..100 -> {
                _state.value = s.copy(error = "La edad debe estar entre 18 y 100 años")
                return
            }
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val current = _state.value

            if (!signedUp) {
                val signUpResult = authRepository.signUp(current.email, current.password)
                if (signUpResult.isFailure) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = signUpResult.exceptionOrNull()?.message ?: "Error al crear la cuenta"
                    )
                    return@launch
                }
                signedUp = true
            }

            if (authenticatedUser == null) {
                val signInResult = authRepository.signIn(current.email, current.password)
                val user = signInResult.getOrElse { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al iniciar sesión"
                    )
                    return@launch
                }
                authenticatedUser = user
            }

            val user = authenticatedUser!!
            val profileResult = profileRepository.createProfile(
                userId = user.id,
                photoUrl = "",
                name = current.name,
                username = current.username,
                address = current.address,
                age = ageInt!!,
                phoneNumber = current.phoneNumber
            )
            profileResult.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al crear el perfil"
                    )
                }
            )
        }
    }
}
