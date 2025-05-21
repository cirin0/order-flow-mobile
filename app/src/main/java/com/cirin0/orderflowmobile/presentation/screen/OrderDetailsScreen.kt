package com.cirin0.orderflowmobile.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.OrderDetailsViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.ErrorView
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun OrderDetailsScreen(
    orderId: String,
    navController: NavHostController,
) {
    val viewModel: OrderDetailsViewModel = hiltViewModel()
    val orderState by viewModel.orderState.collectAsState()
    val scrollState = rememberScrollState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxWidth(),
        onRefresh = { viewModel.loadOrderDetails(orderId) }
    ) {
        when (orderState) {
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
                val order = (orderState as Resource.Success<OrderResponse>).data
                if (order != null) {
                    OrderDetailsContent(
                        order = order,
                        onCancelOrder = { showCancelDialog = true },
                        onPayOrder = {
                            Toast.makeText(
                                navController.context,
                                "Оплата замовлення в даний момент недоступна",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    )
                }
            }

            is Resource.Error -> {
                val errorMessage = when {
                    orderState.message?.contains("timeout", ignoreCase = true) == true ->
                        "Не вдалося завантажити замовлення через повільне з'єднання. Перевірте інтернет та спробуйте знову."

                    orderState.message?.contains("hostname", ignoreCase = true) == true ->
                        "Відсутнє підключення до інтернету. Перевірте налаштування мережі."

                    orderState.message?.contains("Failed", ignoreCase = true) == true ->
                        "Щось пішло не так. Спробуйте ще раз."

                    else -> orderState.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                }

                ErrorView(
                    errorMessage = errorMessage,
                    scrollState = scrollState
                )
            }
        }
    }

    if (showCancelDialog) {
        CancelOrderDialog(
            onConfirm = {
                viewModel.cancelOrder(orderId)
                showCancelDialog = false
            },
            onDismiss = { showCancelDialog = false }
        )
    }
}

@Composable
fun OrderDetailsContent(
    order: OrderResponse,
    onCancelOrder: () -> Unit,
    onPayOrder: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Деталі замовлення №${order.id}",
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
            item {
                OrderInfoCard(order)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Товари у замовленні",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(order.items.size) { index ->
                val product = order.items[index]
                OrderItemCard(
                    productName = product.productName,
                    quantity = product.quantity,
                    price = product.price,
                    totalPrice = product.totalPrice,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

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
                text = "₴${order.totalPrice.toInt()}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OrderActions(
            orderStatus = order.status,
            onCancelOrder = onCancelOrder,
            onPayOrder = onPayOrder,
        )
    }
}


@Composable
fun OrderInfoCard(order: OrderResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ТТН номер: ${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Дата замовлення: ${formatDate(order.orderDate)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Адреса доставки:",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            val address = order.deliveryAddress

            Text(text = "${address.region}, ${address.city}")
            if (address.area.isNotBlank()) {
                Text(text = "Район: ${address.area}")
            }
            Text(text = "вул. ${address.street}, буд. ${address.house}")
            if (address.apartment.isNotBlank()) {
                Text(text = "кв. ${address.apartment}")
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "NEW" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "PAID" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "PROCESSING" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "COMPLETED" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "CANCELED" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "SHIPPED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "DELIVERED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (status) {
        "NEW" -> "Нове"
        "PAID" -> "Оплачено"
        "PROCESSING" -> "В обробці"
        "COMPLETED" -> "Виконано"
        "CANCELED" -> "Скасовано"
        "SHIPPED" -> "Відправлено"
        "DELIVERED" -> "Доставлено"
        else -> status
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = statusText,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrderItemCard(
    productName: String,
    quantity: Int,
    price: Double,
    totalPrice: Double,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(
                    text = productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$quantity x ₴${price.toInt()}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = "₴${totalPrice.toInt()}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun OrderActions(
    orderStatus: String,
    onCancelOrder: () -> Unit,
    onPayOrder: () -> Unit,
) {
    val status = orderStatus.uppercase()
    val isPaid = status == "PAID" || status == "COMPLETED"
    val isCancelled = status == "CANCELED"
    val canCancel = status == "NEW" || status == "PROCESSING"
    val canDownloadPdf = status == "COMPLETED" || status == "DELIVERED" || status == "SHIPPED"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        if (canCancel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelOrder,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Скасувати замовлення")
                }

                Button(
                    onClick = onPayOrder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Оплатити")
                }
            }
        } else if (isPaid) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = "Ваше замовлення оплачено і обробляється",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        } else if (isCancelled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = "Це замовлення було скасовано",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        } else if (canDownloadPdf) {
            Button(
                onClick = { /* TODO: Implement PDF download */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Завантажити PDF")
            }
        }
    }
}

@Composable
fun CancelOrderDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Скасувати замовлення?") },
        text = { Text("Ви впевнені, що хочете скасувати це замовлення?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Так, скасувати")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ні, залишити")
            }
        }
    )
}
