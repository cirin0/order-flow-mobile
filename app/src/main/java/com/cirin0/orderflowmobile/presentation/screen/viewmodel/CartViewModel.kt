package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.domain.repository.CartRepository
import com.cirin0.orderflowmobile.util.Resource
import com.cirin0.orderflowmobile.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _cart = MutableStateFlow<Resource<CartResponse>>(Resource.Loading())
    val cart: StateFlow<Resource<CartResponse>> = _cart.asStateFlow()

    private val _syncingStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val syncingStates: StateFlow<Map<String, Boolean>> = _syncingStates.asStateFlow()

    private val pendingQuantityUpdates = mutableMapOf<String, Int>()
    private val debounceJobs = mutableMapOf<String, Job>()


    fun updateItemQuantityOptimistic(itemId: String, newQuantity: Int) {
        optimisticallyUpdateUI(itemId, newQuantity)
        setSyncingState(itemId, true)
        pendingQuantityUpdates[itemId] = newQuantity
        debounceJobs[itemId]?.cancel()
        debounceJobs[itemId] = viewModelScope.launch {
            delay(300)
            synchronizeItem(itemId)
        }
    }

    private fun optimisticallyUpdateUI(itemId: String, newQuantity: Int) {
        val currentCartValue = _cart.value
        if (currentCartValue is Resource.Success && currentCartValue.data != null) {
            val currentCart = currentCartValue.data
            val updatedItems = currentCart.items.map { item ->
                if (item.id == itemId) item.copy(quantity = newQuantity) else item
            }
            val newTotalPrice = updatedItems.sumOf { it.price * it.quantity }
            val updatedCart = currentCart.copy(
                items = updatedItems,
                totalPrice = newTotalPrice
            )
            _cart.value = Resource.Success(updatedCart)
        }
    }

    private fun setSyncingState(itemId: String, isSyncing: Boolean) {
        val currentStates = _syncingStates.value.toMutableMap()
        currentStates[itemId] = isSyncing
        _syncingStates.value = currentStates
    }

    private suspend fun synchronizeItem(itemId: String) {
        val cartData = (_cart.value as? Resource.Success)?.data
        val cartId = cartData?.id ?: return

        val quantityToSync = pendingQuantityUpdates[itemId] ?: return

        try {
            val result = cartRepository.updateItemInCart(cartId, itemId, quantityToSync)
            if (result is Resource.Success) {
                _cart.value = sortCartItems(result)
                pendingQuantityUpdates.remove(itemId)
            } else {
                loadCart()
            }
        } catch (e: Exception) {
            loadCart()
        } finally {
            setSyncingState(itemId, false)
        }
    }

    init {
        viewModelScope.launch {
            tokenManager.userId.collect { userId ->
                if (userId.isNotBlank()) {
                    loadCart()
                } else {
                    _cart.value = Resource.Error("User not logged in")
                }
            }
        }
    }

    private suspend fun getUserId(): String {
        return tokenManager.userId.firstOrNull() ?: ""
    }

    fun loadCart() {
        viewModelScope.launch {
            val userId = getUserId()
            if (userId.isNotBlank()) {
                _cart.value = Resource.Loading()
                try {
                    val result = cartRepository.getCartByUserId(userId)
                    if (result is Resource.Error) {
                        _cart.value = Resource.Error("Failed to load cart")
                    } else {
                        val sortedResult = sortCartItems(result)
                        _cart.value = sortedResult
                    }
                } catch (e: Exception) {
                    _cart.value = Resource.Error(message = e.message ?: "Unknown error")
                }
            } else {
                _cart.value = Resource.Error("User not logged in")
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val cartData = (_cart.value as? Resource.Success)?.data
            val cartId = cartData?.id ?: return@launch

            val result = cartRepository.removeItemFromCart(cartId, itemId)
            if (result is Resource.Success) {
                val sortedResult = sortCartItems(result)
                _cart.value = sortedResult
            } else {
                _cart.value = Resource.Error("Failed to remove item from cart")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val cartData = (_cart.value as? Resource.Success)?.data
            val cartId = cartData?.id ?: return@launch

            val result = cartRepository.clearCart(cartId)
            if (result is Resource.Success) {
                val sortedResult = sortCartItems(result)
                _cart.value = sortedResult
            } else {
                _cart.value = Resource.Error("Failed to clear cart")
            }
        }
    }

    private fun sortCartItems(resource: Resource<CartResponse>): Resource<CartResponse> {
        if (resource !is Resource.Success || resource.data == null) {
            return resource
        }
        val cartData = resource.data
        val sortedItems = cartData.items.sortedBy { it.id }
        val newCartData = cartData.copy(items = sortedItems)
        return Resource.Success(newCartData)
    }
}
