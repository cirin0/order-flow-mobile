package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    // You would inject auth repository or use case here
) : ViewModel() {

    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: State<Boolean> = _isAuthenticated

    // Example method to update authentication state
    fun updateAuthState(isAuthenticated: Boolean) {
        _isAuthenticated.value = isAuthenticated
    }

    // Initialize - you would check if user is logged in here
    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        // Implementation would depend on your auth mechanism
        // For example, check if a token exists in preferences/secure storage

    }
}