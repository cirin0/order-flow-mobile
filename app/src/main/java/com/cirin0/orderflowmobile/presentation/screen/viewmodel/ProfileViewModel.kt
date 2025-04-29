package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.data.repository.AuthRepositoryImpl
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    tokenManager: TokenManager
) : ViewModel() {

    val isAuthenticated = authRepository.isLoggedIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail = tokenManager.email
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
