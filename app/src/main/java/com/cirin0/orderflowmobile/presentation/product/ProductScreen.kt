package com.cirin0.orderflowmobile.presentation.product

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
    id: String?,
    navController: NavHostController,
) {

    val product by viewModel.product.collectAsState()

    LaunchedEffect(id) {
        viewModel.getProduct(id ?: "")
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

// chesk if stock is 0 then false else true
fun checkStock(stock: Int): Boolean {
    return stock > 0
}

@Composable
fun ProductDetails(product: ProductDetails) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            600 // Height in pixels
                        )
                    }
                },
                update = { imageView ->
                    Glide.with(imageView)
                        .load(product.imageUrl)
                        .centerCrop()
                        .into(imageView)
                }
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "₴${product.price.toInt()}",
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { /* TODO: Add to cart */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Add to cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                }
            }
            Text(
                text = if (checkStock(product.stock)) "В наявності" else "Немає в наявності",
                color = if (checkStock(product.stock)) Color.Green else Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Опис",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
            )
            Text(
                text = "Рейтинг: ${product.averageRating}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
    }
}
