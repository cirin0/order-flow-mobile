package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.FavoriteProduct
import com.cirin0.orderflowmobile.domain.usecase.GetAllFavoritesUseCase
import com.cirin0.orderflowmobile.domain.usecase.RemoveFavoriteUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) : ViewModel() {

    private val _favorites = MutableStateFlow<Resource<List<FavoriteProduct>>>(Resource.Loading())
    val favorites: StateFlow<Resource<List<FavoriteProduct>>> = _favorites

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = Resource.Loading()

            getAllFavoritesUseCase()
                .catch { e ->
                    _favorites.value = Resource.Error(
                        message = e.localizedMessage ?: "Помилка завантаження вибраного"
                    )
                }
                .collectLatest { favoritesList ->
                    _favorites.value = Resource.Success(favoritesList)
                }
        }
    }

    fun removeFavorite(favoriteId: Int) {
        viewModelScope.launch {
            removeFavoriteUseCase(favoriteId)
        }
    }
}
