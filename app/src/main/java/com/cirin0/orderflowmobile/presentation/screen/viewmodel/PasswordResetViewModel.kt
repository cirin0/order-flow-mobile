package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.password.ResetPassword
import com.cirin0.orderflowmobile.domain.model.password.ValidatePasswordCode
import com.cirin0.orderflowmobile.domain.repository.PasswordResetRepository
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

data class PasswordResetState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = "",
    val currentStep: Int = 0
)

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val repository: PasswordResetRepository
) : ViewModel() {
    private val _state = mutableStateOf(PasswordResetState())
    val state: State<PasswordResetState> = _state

    val email = mutableStateOf("")
    val code = mutableStateOf("")
    val newPassword = mutableStateOf("")
    val confirmPassword = mutableStateOf("")

    fun onEmailChanged(value: String) {
        email.value = value
    }

    fun onCodeChanged(value: String) {
        code.value = value
    }

    fun onNewPasswordChanged(value: String) {
        newPassword.value = value
    }

    fun onConfirmPasswordChanged(value: String) {
        confirmPassword.value = value
    }

    fun sendResetEmail() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            try {
                val result = repository.sendPasswordResetEmail(email.value)
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            currentStep = 1,
                            error = ""
                        )
                    }

                    is Resource.Error -> {
                        val errorMessage = when {
                            result.message?.contains("Користувача з таким email не знайдено") == true ->
                                "Користувача з таким email не знайдено. Перевірте правильність email."

                            result.message?.contains("JsonReader.setLenient") == true ->
                                "Помилка обробки відповіді сервера. Спробуйте ще раз."

                            result.message?.contains("timeout", ignoreCase = true) == true ->
                                "Час очікування відповіді сервера вичерпано. Перевірте підключення до інтернету."

                            result.message?.contains(
                                "Unable to resolve host",
                                ignoreCase = true
                            ) == true ->
                                "Немає підключення до інтернету. Перевірте мережеве з'єднання."

                            else -> result.message ?: "Невідома помилка при надсиланні email"
                        }
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is UnknownHostException -> "Немає підключення до інтернету"
                    is SocketTimeoutException -> "Час очікування відповіді сервера вичерпано"
                    else -> e.message ?: "Невідома помилка"
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    fun verifyCode() {
        _state.value = _state.value.copy(isLoading = true, error = "")

        viewModelScope.launch {
            val validateCode = ValidatePasswordCode(email.value, code.value)
            val result = repository.validatePasswordResetCode(validateCode)
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    if (result.data == true) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            currentStep = 2
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Невірний код"
                        )
                    }
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Не вдалося перевірити код"
                    )
                }
            }
        }
    }

    fun resetPassword() {
        if (newPassword.value != confirmPassword.value) {
            _state.value = _state.value.copy(
                error = "Паролі не співпадають"
            )
            return
        }
        _state.value = _state.value.copy(isLoading = true, error = "")

        viewModelScope.launch {
            val resetPasswordData = ResetPassword(
                email = email.value,
                code = code.value,
                newPassword = newPassword.value,
                confirmPassword = confirmPassword.value
            )
            when (val result = repository.resetPassword(resetPasswordData)) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Не вдалося скинути пароль"
                    )
                }
            }
        }

    }

}
