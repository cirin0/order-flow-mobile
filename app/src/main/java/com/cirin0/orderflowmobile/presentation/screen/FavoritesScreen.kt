package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cirin0.orderflowmobile.domain.model.FavoriteProduct
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.FavoriteViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.useRefreshHandler
import com.cirin0.orderflowmobile.util.Resource
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val viewModel: FavoriteViewModel = hiltViewModel()
    val favorites by viewModel.favorites.collectAsState()
    val refreshHandler = useRefreshHandler()
    val scrollState = rememberScrollState()

    LaunchedEffect(favorites) {
        if (refreshHandler.isRefreshing && favorites !is Resource.Loading) {
            refreshHandler.resetRefreshState()
        }
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { viewModel.loadFavorites() }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Вибране",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when (favorites) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is Resource.Success -> {
                    val favoritesList = favorites.data ?: emptyList()
                    if (favoritesList.isEmpty()) {
                        EmptyFavoritesList(
                            scrollState = scrollState
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(favoritesList.size) { index ->
                                val favorite = favoritesList[index]
                                FavoriteItem(
                                    favorite = favorite,
                                    scrollState = scrollState,
                                    onItemClick = {
                                        navController.navigate("product/${favorite.id}")
                                    },
                                    onDeleteClick = {
                                        viewModel.removeFavorite(favorite.id)
                                    }
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    val errorMessage = when {
                        favorites.message?.contains("timeout", ignoreCase = true) == true ->
                            "Не вдалося завантажити товар через повільне з'єднання. Будь ласка, перевірте підключення до інтернету та спробуйте знову."

                        favorites.message?.contains("hostname", ignoreCase = true) == true ->
                            "Відсутнє підключення до інтернету. Перевірте налаштування мережі та спробуйте знову."

                        else -> favorites.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                    }
                    ErrorView(errorMessage = errorMessage, scrollState = scrollState)
                }
            }
        }
    }
}


@Composable
fun FavoriteItem(
    favorite: FavoriteProduct,
    scrollState: ScrollState,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    LaunchedEffect(scrollState) {
        scrollState.scrollTo(0)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                GlideImage(
                    imageModel = { favorite.imageUrl },
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    },
                    failure = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = favorite.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₴${favorite.price.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити з вибраного",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesList(
    scrollState: ScrollState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ваш список вибраного порожній",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Додайте товари до вибраного, щоб бачити їх тут",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
