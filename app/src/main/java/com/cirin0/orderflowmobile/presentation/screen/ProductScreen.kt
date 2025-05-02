package com.cirin0.orderflowmobile.presentation.screen

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.cirin0.orderflowmobile.domain.model.ProductDetails
import com.cirin0.orderflowmobile.domain.model.review.ReviewResponse
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.ProductViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.useRefreshHandler
import com.cirin0.orderflowmobile.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    id: String?,
    navController: NavHostController,
) {
    val viewModel: ProductViewModel = hiltViewModel()
    val product by viewModel.product.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    val refreshHandler = useRefreshHandler()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = id) {
        id?.let { productId ->
            viewModel.getProduct(productId)
            viewModel.getReviews(productId)
        }
    }

    LaunchedEffect(product) {
        if (refreshHandler.isRefreshing && product !is Resource.Loading) {
            refreshHandler.resetRefreshState()
        }
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            id?.let { productId ->
                viewModel.refreshData(productId)
                viewModel.getReviews(productId)
            }
        },
    ) {
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
                    ProductDetails(
                        product = data,
                        scrollState = scrollState,
                        isFavorite = isFavorite,
                        reviews = reviews,
                        onToggleFavorite = { viewModel.toggleFavorite() })
                } else {
                    Text("Товар не знайдено", color = MaterialTheme.colorScheme.error)
                }
            }

            is Resource.Error -> {
                val errorMessage = when {
                    product.message?.contains("timeout", ignoreCase = true) == true ->
                        "Не вдалося завантажити товар через повільне з'єднання. Будь ласка, перевірте підключення до інтернету та спробуйте знову."

                    product.message?.contains("hostname", ignoreCase = true) == true ->
                        "Відсутнє підключення до інтернету. Перевірте налаштування мережі та спробуйте знову."

                    else -> product.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                }
                ErrorView(errorMessage = errorMessage, scrollState = scrollState)
            }
        }
    }
}


@Composable
fun ErrorView(errorMessage: String, scrollState: ScrollState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.size(100.dp))
        }
    }
}

fun checkStock(stock: Int): Boolean {
    return stock > 0
}

@SuppressLint("DefaultLocale")
@Composable
fun ProductDetails(
    product: ProductDetails,
    scrollState: ScrollState,
    isFavorite: Boolean,
    reviews: Resource<List<ReviewResponse>>,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .padding(bottom = 50.dp)
                .verticalScroll(scrollState)
        ) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1000
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
                    onClick = { onToggleFavorite() }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Видалити з улюблених" else "Додати в улюблене",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
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
                text = "Рейтинг: ${String.format("%.1f", product.averageRating)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Text(
                text = "Відгуки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            when (reviews) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is Resource.Success -> {
                    val reviewList = reviews.data ?: emptyList()
                    if (reviewList.isEmpty()) {
                        Text(
                            text = "Немає відгуків",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        ReviewsList(reviews = reviews)
                    }
                }

                is Resource.Error -> {
                    Text(
                        text = "Не вдалося завантажити відгуки: ${reviews.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

            }
        }
        Button(
            onClick = {
                if (!checkStock(product.stock)) {
                    Log.d("ProductDetails", "Товар недоступний")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (checkStock(product.stock)) {
                Text(
                    text = "Додати в кошик",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Text(
                    text = "Товар недоступний",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ReviewsList(reviews: Resource.Success<List<ReviewResponse>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        reviews.data?.forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}

@Composable
fun ReviewItem(review: ReviewResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${review.userFirst_name} ${review.userLast_name}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column {
                Text(
                    text = formatDate(review.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingStars(rating = review.rating)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = review.content,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp)
                .size(height = 1.dp, width = 0.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun RatingStars(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        if (parts.isNotEmpty()) {
            parts[0].replace("-", ".")
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

