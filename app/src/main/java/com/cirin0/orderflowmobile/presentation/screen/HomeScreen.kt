package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.HomeViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.useRefreshHandler
import com.cirin0.orderflowmobile.util.Resource
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val products by viewModel.products.collectAsState()
    val category by viewModel.categories.collectAsState()
    val refreshHandler = useRefreshHandler()
    val favoriteStatus by viewModel.productFavoriteStatus.collectAsState()

    LaunchedEffect(products, category) {
        if (refreshHandler.isRefreshing &&
            products !is Resource.Loading &&
            category !is Resource.Loading
        ) {
            refreshHandler.resetRefreshState()
        }
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { viewModel.refreshData() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                when (category) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Resource.Success -> {
                        val data = category.data ?: emptyList()
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 5.dp),
                        ) {
                            items(data.size) { index ->
                                val categoryItem = data[index]
                                CategoryRow(category = categoryItem) {
                                    navController.navigate("category/${categoryItem.name}")
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        Text(
                            text = category.message ?: "Unknown error",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            when (products) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Resource.Success -> {
                    val data = products.data ?: emptyList()
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(data.size) { index ->
                            val product = data[index]
                            val isFavorite = favoriteStatus[product.id] ?: false
                            ProductCard(
                                product = product,
                                isFavorite = isFavorite,
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(product)
                                },
                            ) {
                                navController.navigate("product/${product.id}")
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Text(
                        text = products.message ?: "Unknown error",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(
                        RoundedCornerShape(5.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                GlideImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    imageModel = { product.imageUrl },
                    loading = {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    },
                    failure = {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Failed to load image")
                        }
                    },
                )
            }
            Text(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                fontSize = 25.sp,
                text = product.name,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    text = "₴${product.price.toInt()}",
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onFavoriteClick() },
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Видалити з вибраного" else "Додати до вибраного",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = category.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
