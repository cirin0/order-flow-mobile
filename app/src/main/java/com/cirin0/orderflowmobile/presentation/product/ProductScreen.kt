package com.cirin0.orderflowmobile.presentation.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
    id: Int?,
    navController: NavHostController,
) {

    val product = viewModel.product.value
    
    LaunchedEffect(id) {
        viewModel.loadProduct(id)
    }

    when (product) {
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
            val data = product.data
            if (data != null) {
                ProductDetails(product = data)
            } else {
                Text("Товар не знайдено", color = MaterialTheme.colorScheme.error)
            }
        }

        is Resource.Error -> {
            Text(product.message ?: "Unknown error")
        }
    }
}

@Composable
fun ProductDetails(product: ProductDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Ціна: ${product.price}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Категорія: ${product.categoryName}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Рейтинг: ${product.averageRating}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
