package com.cirin0.orderflowmobile.presentation.screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.domain.usecase.GetProductByCategoryNameUseCase
import com.cirin0.orderflowmobile.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getProductByCategoryNameUseCase: GetProductByCategoryNameUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _category = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val category: MutableStateFlow<Resource<List<Product>>> = _category

    init {
        savedStateHandle.get<String>("categoryName")?.let { categoryName ->
            getCategory(categoryName)
        }
    }

    fun getCategory(name: String) {
        viewModelScope.launch {
            _category.value = Resource.Loading()
            _category.value = getProductByCategoryNameUseCase(name)
        }
    }

    fun refreshData(name: String) {
        getCategory(name)
    }
}
