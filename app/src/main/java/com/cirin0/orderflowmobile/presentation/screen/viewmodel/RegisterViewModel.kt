package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.user.AuthResponse
import com.cirin0.orderflowmobile.domain.usecase.RegisterUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    private val _firstName = mutableStateOf("")
    val firstName: State<String> = _firstName

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> = _isEmailValid

    private val _isPasswordValid = mutableStateOf(true)
    val isPasswordValid: State<Boolean> = _isPasswordValid

    private val _isFirsNameValid = mutableStateOf(true)
    val isFirstNameValid: State<Boolean> = _isFirsNameValid

    private val _isLastNameValid = mutableStateOf(true)
    val isLastNameValid: State<Boolean> = _isLastNameValid


    fun onFirstNameChanged(newFirstName: String) {
        _firstName.value = newFirstName
        _isFirsNameValid.value =
            validateFirstAndLastName(newFirstName) || newFirstName.isEmpty()
    }

    fun onLastNameChanged(newLastName: String) {
        _lastName.value = newLastName
        _isLastNameValid.value =
            validateFirstAndLastName(newLastName) || newLastName.isEmpty()
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = validateEmail(newEmail) || newEmail.isEmpty()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = validatePassword(newPassword) || newPassword.isEmpty()
    }

    fun register() {
        registerUseCase(
            firstName = firstName.value,
            lastName = lastName.value,
            email = email.value,
            password = password.value
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = RegisterState(
                        isSuccess = true,
                        data = result.data
                    )
                }

                is Resource.Error -> {
                    _state.value = RegisterState(
                        error = result.message ?: "Невідома помилка"
                    )
                }

                is Resource.Loading -> {
                    _state.value = RegisterState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 5
    }

    private fun validateFirstAndLastName(string: String): Boolean {
        return string.length >= 2 && string.all { it.isLetter() }
    }
}

data class RegisterState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val data: AuthResponse? = null,
)
