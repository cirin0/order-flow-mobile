package com.cirin0.orderflowmobile.presentation.screen

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cirin0.orderflowmobile.domain.model.cart.CartItem
import com.cirin0.orderflowmobile.domain.model.order.OrderAddress
import com.cirin0.orderflowmobile.presentation.navigation.NavRoutes
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.OrderViewModel
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.ProfileViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.ErrorView
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun OrderScreen(
    navController: NavHostController,
) {
    val viewModel: OrderViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val cartState by viewModel.cartState.collectAsState()
    val orderState by viewModel.orderState.collectAsState()
    val processingOrder by viewModel.processingOrder.collectAsState()
    val scrollState = rememberScrollState()
    val userProfile by profileViewModel.userProfile.collectAsState()

    var showAddressForm by remember { mutableStateOf(false) }
    var region by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var house by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }

    var hasProfileAddress by remember { mutableStateOf(false) }

    LaunchedEffect(userProfile) {
        if (userProfile is Resource.Success) {
            val userData = userProfile.data
            userData?.let {
                hasProfileAddress = it.address != null
                it.address?.let { address ->
                    region = address.region
                    city = address.city
                    area = address.area
                    street = address.street
                    house = address.house
                    apartment = address.apartment
                }
            }
        }
    }

    LaunchedEffect(orderState) {
        val currentOrderState = orderState
        if (currentOrderState is Resource.Success && currentOrderState.data != null) {
            val orderId = currentOrderState.data.id
            navController.navigate("${NavRoutes.ORDER_DETAILS}/${orderId}") {
                popUpTo(NavRoutes.CART) { inclusive = true }
            }
        }
    }

    when (cartState) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            val cartData = (cartState as Resource.Success).data
            val userData = (userProfile as? Resource.Success)?.data
            if (cartData != null && userData != null) {
                CheckoutContent(
                    cartItems = cartData.items,
                    totalAmount = cartData.totalPrice,
                    isProcessing = processingOrder,
                    userName = (userProfile.data?.firstName + " " + userProfile.data?.lastName),
                    userPhone = userProfile.data?.phone ?: "",
                    userEmail = userProfile.data?.email ?: "",
                    showAddressForm = showAddressForm,
                    hasProfileAddress = hasProfileAddress,
                    region = region,
                    city = city,
                    area = area,
                    street = street,
                    house = house,
                    apartment = apartment,
                    onShowAddressForm = { showAddressForm = true },
                    onRegionChange = { region = it },
                    onCityChange = { city = it },
                    onAreaChange = { area = it },
                    onStreetChange = { street = it },
                    onHouseChange = { house = it },
                    onApartmentChange = { apartment = it },
                    onConfirmOrder = {
                        if (showAddressForm) {
                            val newAddress = OrderAddress(
                                region = region,
                                city = city,
                                area = area,
                                street = street,
                                house = house,
                                apartment = apartment
                            )
                            viewModel.createOrderWithNewAddress(newAddress)
                        } else {
                            viewModel.createOrder()
                        }
                    }
                )
            }
        }

        is Resource.Error -> {
            val errorMessage = when {
                cartState.message?.contains("timeout", ignoreCase = true) == true ->
                    "Не вдалося завантажити кошик через повільне з'єднання. Перевірте інтернет та спробуйте знову."

                cartState.message?.contains("hostname", ignoreCase = true) == true ->
                    "Відсутнє підключення до інтернету. Перевірте налаштування мережі."

                cartState.message?.contains("Failed", ignoreCase = true) == true ->
                    "Щось пішло не так. Спробуйте ще раз."

                else -> cartState.message ?: "Сталася невідома помилка. Спробуйте пізніше."
            }
            ErrorView(
                errorMessage = errorMessage,
                scrollState = scrollState
            )
        }
    }
}

@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    totalAmount: Double,
    isProcessing: Boolean,
    userName: String,
    userPhone: String,
    userEmail: String,
    showAddressForm: Boolean,
    hasProfileAddress: Boolean,
    region: String,
    city: String,
    area: String,
    street: String,
    house: String,
    apartment: String,
    onShowAddressForm: () -> Unit,
    onRegionChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onHouseChange: (String) -> Unit,
    onApartmentChange: (String) -> Unit,
    onConfirmOrder: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(12.dp)
                .padding(bottom = 60.dp),
        ) {
            item {
                Text(
                    text = "Оформлення замовлення",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Контактна інформація",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                UserInfoSection(userName, userPhone, userEmail)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Адреса доставки",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (!showAddressForm) {
                        Button(
                            onClick = onShowAddressForm,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(if (hasProfileAddress) "Редагувати адресу" else "Додати адресу")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (showAddressForm) {
                    AddressForm(
                        region = region,
                        city = city,
                        area = area,
                        street = street,
                        house = house,
                        apartment = apartment,
                        onRegionChange = onRegionChange,
                        onCityChange = onCityChange,
                        onAreaChange = onAreaChange,
                        onStreetChange = onStreetChange,
                        onHouseChange = onHouseChange,
                        onApartmentChange = onApartmentChange
                    )
                } else if (hasProfileAddress) {
                    AddressSummary(region, city, area, street, house, apartment)
                } else {
                    Text(
                        "Адреса не вказана. Натисніть кнопку «Додати адресу», щоб продовжити.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Ваше замовлення",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(cartItems) { item ->
                OrderItemRow(item)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Загальна сума:",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "₴${totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp
                    )
                }
            }
        }
        val isFormValid = if (showAddressForm) {
            region.isNotBlank() && city.isNotBlank() &&
                street.isNotBlank() && house.isNotBlank()
        } else {
            hasProfileAddress
        }
        Button(
            onClick = onConfirmOrder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(8.dp),
            enabled = !isProcessing && isFormValid
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "Підтвердити замовлення",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
fun UserInfoSection(name: String, phone: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Ім'я: $name")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Телефон: $phone")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Email: $email")
        }
    }
}

@Composable
fun AddressSummary(
    region: String,
    city: String,
    area: String,
    street: String,
    house: String,
    apartment: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Область: $region")
            Text("Місто: $city")
            if (area.isNotBlank()) {
                Text("Район: $area")
            }
            Text("Вулиця: $street")
            Text("Будинок: $house")
            if (apartment.isNotBlank()) {
                Text("Квартира: $apartment")
            }
        }
    }
}

@Composable
fun AddressForm(
    region: String,
    city: String,
    area: String,
    street: String,
    house: String,
    apartment: String,
    onRegionChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onHouseChange: (String) -> Unit,
    onApartmentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = region,
            onValueChange = onRegionChange,
            label = { Text("Область*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("Місто*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = area,
            onValueChange = onAreaChange,
            label = { Text("Район*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = street,
            onValueChange = onStreetChange,
            label = { Text("Вулиця*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = house,
                onValueChange = onHouseChange,
                label = { Text("Будинок*") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = apartment,
                onValueChange = onApartmentChange,
                label = { Text("Квартира") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.productName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.quantity} x ₴${item.price.toInt()}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "₴${(item.quantity * item.price).toInt()}",
            fontWeight = FontWeight.Bold
        )
    }
}
