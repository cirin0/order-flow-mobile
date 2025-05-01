package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.data.repository.AuthRepositoryImpl
import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import com.cirin0.orderflowmobile.domain.usecase.GetUserDataUseCase
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val getUserDataUseCase: GetUserDataUseCase,
    tokenManager: TokenManager
) : ViewModel() {

    val isAuthenticated = authRepository.isLoggedIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail = tokenManager.email
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userId = tokenManager.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private val _userProfile = MutableStateFlow<Resource<UserResponse>>(Resource.Loading())
    val userProfile: StateFlow<Resource<UserResponse>> = _userProfile

    private val _addressOperation = MutableStateFlow<Resource<AddressItem>?>(null)
    val addressOperation: StateFlow<Resource<AddressItem>?> = _addressOperation.asStateFlow()

    init {
        viewModelScope.launch {
            userEmail.collect { email ->
                if (email.isNotBlank()) {
                    loadUserProfile()
                }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val email = userEmail.value
            _userProfile.value = Resource.Loading()
            try {
                val result = getUserDataUseCase(email)
                _userProfile.value = result
            } catch (e: Exception) {
                _userProfile.value = Resource.Error(message = e.message ?: "Unknown error")
            }
        }
    }

//    fun addAddress(addressRequest: AddressRequest) {
//        viewModelScope.launch {
//            val id = userId.value
//            if (id.isNotBlank()) {
//                userRepository.addAddress(id, addressRequest).collect { result ->
//                    _addressOperation.value = result
//                }
//            }
//        }
//    }
//
//    fun updateAddress(addressId: Int, addressRequest: AddressRequest) {
//        viewModelScope.launch {
//            val id = userId.value
//            if (id.isNotBlank()) {
//                userRepository.updateAddress(id, addressId, addressRequest).collect { result ->
//                    _addressOperation.value = result
//                }
//            }
//        }
//    }

    fun clearAddressOperation() {
        _addressOperation.value = null
    }
}
