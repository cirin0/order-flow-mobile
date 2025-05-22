package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.user.AuthResponse
import com.cirin0.orderflowmobile.domain.usecase.LoginUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> = _isEmailValid

    private val _isPasswordValid = mutableStateOf(true)
    val isPasswordValid: State<Boolean> = _isPasswordValid

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = validateEmail(newEmail) || newEmail.isEmpty()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = validatePassword(newPassword) || newPassword.isEmpty()
    }

    fun login() {
        loginUseCase(email.value, password.value).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = LoginState(
                        isSuccess = true,
                        data = result.data
                    )
                }

                is Resource.Error -> {
                    val errorMessage = when {
                        else -> result.message ?: "Помилка з'єднання з сервером"
                    }
                    _state.value = LoginState(
                        error = errorMessage,
                    )
                }

                is Resource.Loading -> {
                    _state.value = LoginState(isLoading = true)
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
}

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val data: AuthResponse? = null
)
