package com.cirin0.orderflowmobile.presentation.screen.viewmodel

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

    private val _firstName = mutableStateOf("Тест")
    val firstName: State<String> = _firstName

    private val _lastName = mutableStateOf("Тестовий")
    val lastName: State<String> = _lastName

    private val _email = mutableStateOf("test@gmail.com")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    fun onFirstNameChanged(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun onLastNameChanged(newLastName: String) {
        _lastName.value = newLastName
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
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
}

data class RegisterState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val data: AuthResponse? = null,
)
