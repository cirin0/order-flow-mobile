package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.CategoryViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.useRefreshHandler
import com.cirin0.orderflowmobile.util.Resource


@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    name: String?,
    navController: NavController
) {
    val viewModel: CategoryViewModel = hiltViewModel()
    val category by viewModel.category.collectAsState()
    val refreshHandler = useRefreshHandler()
    val favoriteStatus by viewModel.productFavoriteStatus.collectAsState()

    LaunchedEffect(key1 = name) {
        name?.let { categoryName ->
            viewModel.getCategory(categoryName)
        }

        if (refreshHandler.isRefreshing &&
            category !is Resource.Loading
        ) {
            refreshHandler.resetRefreshState()
        }
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { viewModel.refreshData(name.toString()) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (category) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is Resource.Success -> {
                    val data = category.data
                    if (data != null) {
                        CategoryList(
                            products = data,
                            favoriteStatus = favoriteStatus,
                            onToggleFavorite = { product -> viewModel.toggleFavorite(product) },
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            }
                        )
                    } else {
                        Text("Товар не знайдено", color = MaterialTheme.colorScheme.error)
                    }
                }

                is Resource.Error -> {
                    Text(category.message ?: "Unknown error")
                }
            }
        }
    }

}

@Composable
fun CategoryList(
    products: List<Product>,
    favoriteStatus: Map<Int, Boolean>,
    onToggleFavorite: (Product) -> Unit,
    onProductClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(products.size) { index ->
            val product = products[index]
            ProductCard(
                product = product,
                isFavorite = favoriteStatus[product.id] ?: false,
                onFavoriteClick = { onToggleFavorite(product) },
            ) {
                onProductClick(product.id.toString())
            }
        }
    }
}
