package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.SearchResult
import com.cirin0.orderflowmobile.domain.usecase.SearchUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val searchResult: SearchResult? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class MainViewModel @Inject constructor(
    // You would inject auth repository or use case here
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: State<Boolean> = _isAuthenticated

    private val _searchState = MutableStateFlow<SearchState>(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

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

    fun search(query: String) {
        if (query.isBlank()) {
            // Clear search results if query is empty
            _searchState.value = SearchState()
            return
        }
        viewModelScope.launch {
            searchUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _searchState.value = SearchState(isLoading = true)
                    }

                    is Resource.Success -> {
                        _searchState.value = SearchState(
                            searchResult = result.data,
                            isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _searchState.value = SearchState(
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun clearSearchResults() {
        _searchState.value = SearchState(
            isLoading = false,
            searchResult = null,
            error = ""
        )
    }
}
