package com.cirin0.orderflowmobile.presentation.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cirin0.orderflowmobile.domain.model.order.OrderItem
import com.cirin0.orderflowmobile.domain.model.order.OrderResponse
import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.presentation.navigation.NavRoutes
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.UserOrdersViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.ErrorView
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun UserOrdersScreen(
    navController: NavHostController
) {
    val viewModel: UserOrdersViewModel = hiltViewModel()
    val orders by viewModel.orders.collectAsState()
    val scrollState = rememberScrollState()
    var sortCriteria by remember { mutableStateOf(SortCriteria.DATE_DESC) }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { viewModel.loadOrder() },
    ) {
        when (orders) {
            is Resource.Error -> {
                val errorMessage = when {
                    orders.message?.contains("timeout", ignoreCase = true) == true ->
                        "Не вдалося завантажити товар через повільне з'єднання. Будь ласка, перевірте підключення до інтернету та спробуйте знову."

                    orders.message?.contains("hostname", ignoreCase = true) == true ->
                        "Відсутнє підключення до інтернету. Перевірте налаштування мережі та спробуйте знову."

                    else -> orders.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                }
                ErrorView(errorMessage = errorMessage, scrollState = scrollState)
            }

            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is Resource.Success -> {
                val successOrders = orders as Resource.Success<List<OrderResponse>>
                Box {
                    SortingOptions(
                        selectedSortCriteria = sortCriteria,
                        onSortCriteriaSelected = { newSortCriteria ->
                            sortCriteria = newSortCriteria
                        }
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .padding(top = 45.dp)
                    ) {
                        item {
                            OrderList(
                                orders = sortOrders(successOrders.data, sortCriteria),
                                navController = navController
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

enum class SortCriteria {
    DATE_DESC, DATE_ASC, STATUS
}

@Composable
fun SortingOptions(
    selectedSortCriteria: SortCriteria,
    onSortCriteriaSelected: (SortCriteria) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сортувати за: ${
                    when (selectedSortCriteria) {
                        SortCriteria.DATE_DESC -> "Найновіші спочатку"
                        SortCriteria.DATE_ASC -> "Найстаріші спочатку"
                        SortCriteria.STATUS -> "Статусом"
                    }
                }",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Sort options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            DropdownMenuItem(
                text = { Text("Найновіші спочатку") },
                onClick = {
                    onSortCriteriaSelected(SortCriteria.DATE_DESC)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Найстаріші спочатку") },
                onClick = {
                    onSortCriteriaSelected(SortCriteria.DATE_ASC)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("За статусом") },
                onClick = {
                    onSortCriteriaSelected(SortCriteria.STATUS)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun OrderList(
    orders: List<OrderResponse>,
    navController: NavHostController
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "У вас ще немає замовлень",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            orders.forEach { order ->
                OrderItem(
                    order = order,
                    onClick = {
                        navController.navigate("${NavRoutes.ORDER_DETAILS}/${order.id}") {
                            popUpTo(NavRoutes.USER_ORDERS) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

fun sortOrders(orders: List<OrderResponse>?, sortCriteria: SortCriteria): List<OrderResponse> {
    if (orders == null) return emptyList()

    return when (sortCriteria) {
        SortCriteria.DATE_DESC -> orders.sortedByDescending { parseOrderDate(it.orderDate) }
        SortCriteria.DATE_ASC -> orders.sortedBy { parseOrderDate(it.orderDate) }
        SortCriteria.STATUS -> orders.sortedWith(
            compareBy(
                { getStatusPriority(it.status) },
                { parseOrderDate(it.orderDate) }
            ))
    }
}

private fun parseOrderDate(dateString: String): Long {
    return try {
        val dateFormat =
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val date = dateFormat.parse(dateString)
        date?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private fun getStatusPriority(status: String): Int {
    return when (status.lowercase()) {
        "new" -> 1
        "processing" -> 2
        "shipped" -> 3
        "delivered" -> 4
        "completed" -> 5
        "canceled" -> 6
        else -> 7
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun OrderItem(
    order: OrderResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Замовлення № ${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                StatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Дата: ${formatDate(order.orderDate)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            DeliveryAddressSection(address = order.deliveryAddress)

            Spacer(modifier = Modifier.height(8.dp))

            OrderItemsList(items = order.items)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Загальна сума: ${String.format("%.2f", order.totalPrice)} ₴",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun StatusChip(status: String) {
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

@SuppressLint("DefaultLocale")
@Composable
fun OrderItemsList(items: List<OrderItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Товари:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${index + 1}. ${item.productName} (${item.quantity} шт.)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = String.format("%.2f", item.totalPrice) + " ₴",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (index < items.size - 1) {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun DeliveryAddressSection(address: AddressItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Адреса доставки:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "${address.region}, ${address.city}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (address.area.isNotBlank()) {
                    Text(
                        text = "Район: ${address.area}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "вул. ${address.street}, буд. ${address.house}" +
                        (if (address.apartment.isNotBlank()) ", кв. ${address.apartment}" else ""),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
