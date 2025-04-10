package com.cirin0.orderflowmobile.presentation.home

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.cirin0.orderflowmobile.domain.model.Product
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val products by viewModel.products.collectAsState()
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
                    onClick = { /* TODO: Add to cart */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Add to cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp)
                    )
                }

            }
        }
    }
}













