package com.cirin0.orderflowmobile.presentation.home

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val products = viewModel.products.value

    when (products) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            val data = products.data ?: emptyList()
            LazyColumn {
                items(data.size) { index ->
                    val product = data[index]
                    ProductCard(product = product) {
                        navController.navigate("product/${product.id}")
                    }
                }
            }
        }

        is Resource.Error -> {
            Text(products.message ?: "Unknown error")
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
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
                            .centerInside()
                            .into(imageView)
                    }
                )
            }
            Text(
                text = "Ціна: ${product.price}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}