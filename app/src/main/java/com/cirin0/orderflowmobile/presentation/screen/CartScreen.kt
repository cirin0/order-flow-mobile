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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.cart.CartResponse
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.CartViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.ErrorView
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.StyledButton
import com.cirin0.orderflowmobile.util.Resource
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit,
) {
    val viewModel: CartViewModel = hiltViewModel()
    val cart by viewModel.cart.collectAsState()
    val syncingStates by viewModel.syncingStates.collectAsState()
    val scrollState = rememberScrollState()

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxWidth(),
        onRefresh = { viewModel.loadCart() }
    ) {
        when (cart) {
            is Resource.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is Resource.Success -> {
                val cartData = (cart as Resource.Success).data
                if (cartData != null && cartData.items.isNotEmpty()) {
                    CartContent(
                        cart = cartData,
                        syncingStates = syncingStates,
                        onUpdateQuantity = { itemId, quantity ->
                            viewModel.updateItemQuantityOptimistic(itemId, quantity)
                        },
                        onRemoveItem = { itemId ->
                            viewModel.removeItem(itemId)
                        },
                        onClearCart = {
                            viewModel.clearCart()
                        },
                        onCheckout = onNavigateToCheckout
                    )
                } else {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyCartView(
                            onContinueShopping = onNavigateToHome,
                            scrollState = scrollState
                        )
                    }
                }
            }

            is Resource.Error -> {
                if (cart.message?.contains("logged", ignoreCase = true) == true) {
                    NotLoggedInView(
                        onLogin = onNavigateToLogin
                    )
                } else {
                    val errorMessage = when {
                        cart.message?.contains("timeout", ignoreCase = true) == true ->
                            "Не вдалося завантажити кошик через повільне з'єднання. Перевірте інтернет та спробуйте знову."

                        cart.message?.contains("hostname", ignoreCase = true) == true ->
                            "Відсутнє підключення до інтернету. Перевірте налаштування мережі."

                        cart.message?.contains("Failed", ignoreCase = true) == true ->
                            "Щось пішло не так. Спробуйте ще раз."

                        else -> cart.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                    }
                    ErrorView(
                        errorMessage = errorMessage,
                        scrollState = scrollState
                    )
                }
            }
        }
    }
}

@Composable
fun CartContent(
    cart: CartResponse,
    syncingStates: Map<String, Boolean> = emptyMap(),
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit
) {
    var showClearCartDialog by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Кошик",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(
                count = cart.items.size,
                key = { index -> cart.items[index].id }
            ) { index ->
                val item = cart.items[index]
                val isSyncing = syncingStates[item.id] ?: false
                CartItemCard(
                    item = item,
                    onQuantityChange = { quantity ->
                        onUpdateQuantity(item.id, quantity)
                    },
                    onRemoveItem = { onRemoveItem(item.id) },
                    isSyncing = isSyncing
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Загальна сума:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "₴${cart.totalPrice.toInt()}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showClearCartDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Очистити кошик")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Оформити замовлення",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text("Очистити кошик?") },
            modifier = Modifier.padding(1.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCart()
                        showClearCartDialog = false
                    }
                ) {
                    Text("Так")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Скасувати")
                }
            }
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    isSyncing: Boolean = false
) {
    val currentQuantity = remember(item.quantity) { item.quantity }
    val stockQuantity = remember(item.stockQuantity) { item.stockQuantity }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                GlideImage(
                    imageModel = { item.productImageUrl },
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
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "product image",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(
                    text = item.productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₴${item.price.toInt()}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "В наявності: $stockQuantity",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedIconButton(
                        onClick = {
                            if (currentQuantity > 1) {
                                onQuantityChange(currentQuantity - 1)
                            }
                        },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Зменшити",
                            modifier = Modifier
                                .size(35.dp)
                                .background(
                                    color = if (!isSyncing)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(11.dp)
                                )
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.width(30.dp)
                    ) {
                        Text(
                            text = currentQuantity.toString(),
                            modifier = Modifier.padding(horizontal = 4.dp),
                            fontWeight = FontWeight.Bold
                        )

                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(1.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    OutlinedIconButton(
                        onClick = {
                            if (currentQuantity < stockQuantity) {
                                onQuantityChange(currentQuantity + 1)
                            }
                        },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Збільшити",
                            modifier = Modifier
                                .size(35.dp)
                                .background(
                                    color = if (!isSyncing)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(11.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .clickable(enabled = !isSyncing) { onRemoveItem() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Видалити з кошика",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartView(
    onContinueShopping: () -> Unit,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Ваш кошик порожній",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Додайте товари до кошика, щоб продовжити покупки",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        StyledButton(
            modifier = Modifier
                .padding(vertical = 25.dp)
                .fillMaxWidth(),
            onClick = onContinueShopping,
            content = {
                Text(
                    text = "Продовжити покупки",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
        )
    }
}

@Composable
fun NotLoggedInView(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DotLottieAnimation(
            source = DotLottieSource.Url("https://lottie.host/611f3e4e-e3e9-4c54-968d-e5b4dfe9d019/ms9kuYLxL7.lottie"),
            autoplay = true,
            loop = false,
            speed = 2f,
            useFrameInterpolation = false,
            playMode = Mode.FORWARD,
            modifier = Modifier
                .size(200.dp)
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Необхідно увійти до системи",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Щоб переглянути кошик, будь ласка, увійдіть у свій обліковий запис",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        StyledButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = onLogin,
            content = {
                Text(
                    text = "Увійти",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}
