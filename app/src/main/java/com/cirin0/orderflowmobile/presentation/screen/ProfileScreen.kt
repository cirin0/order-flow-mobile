package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cirin0.orderflowmobile.R
import com.cirin0.orderflowmobile.domain.model.user.AddressItem
import com.cirin0.orderflowmobile.domain.model.user.AddressRequest
import com.cirin0.orderflowmobile.domain.model.user.UserResponse
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.ProfileViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.ErrorView
import com.cirin0.orderflowmobile.presentation.ui.component.PullToRefreshWrapper
import com.cirin0.orderflowmobile.presentation.ui.component.useRefreshHandler
import com.cirin0.orderflowmobile.util.Resource

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState(initial = false)
    val scrollState = rememberScrollState()
    val user by viewModel.userProfile.collectAsState()
    val refreshHandler = useRefreshHandler()

    LaunchedEffect(user) {
        if (refreshHandler.isRefreshing && user !is Resource.Loading) {
            refreshHandler.resetRefreshState()
        }
    }

    PullToRefreshWrapper(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { viewModel.loadUserProfile() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            if (isAuthenticated) {
                Text(
                    text = "Мій профіль",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                when (user) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    is Resource.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                val userData = user.data
                                userData?.let { UserProfileContent(it, viewModel) }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.logout() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.logout))
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    is Resource.Error -> {
                        val errorMessage = when {
                            user.message?.contains("timeout", ignoreCase = true) == true ->
                                "Не вдалося завантажити товар через повільне з'єднання. Будь ласка, перевірте підключення до інтернету та спробуйте знову."

                            user.message?.contains("hostname", ignoreCase = true) == true ->
                                "Відсутнє підключення до інтернету. Перевірте налаштування мережі та спробуйте знову."

                            else -> user.message ?: "Сталася невідома помилка. Спробуйте пізніше."
                        }
                        ErrorView(errorMessage = errorMessage, scrollState = scrollState)
                    }
                }
            } else {
                UnauthenticatedUserContent(
                    onLoginClick = onNavigateToLogin,
                    onRegisterClick = onNavigateToRegister,
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
fun UserProfileContent(
    userProfile: UserResponse,
    viewModel: ProfileViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProfileInfoItem("Ім'я", "${userProfile.firstName} ${userProfile.lastName}")
            ProfileInfoItem("Електронна пошта", userProfile.email)
            ProfileInfoItem("Номер телефону", userProfile.phone ?: "Не вказано")
            AddressSection(userProfile.address, viewModel)
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddressSection(
    address: AddressItem?,
    viewModel: ProfileViewModel
) {
    val addressOperationState by viewModel.addressOperation.collectAsState()
    var showAddressDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<AddressItem?>(null) }

    LaunchedEffect(addressOperationState) {
        if (addressOperationState is Resource.Success) {
            showAddressDialog = false
            editingAddress = null
            viewModel.loadUserProfile()
            viewModel.clearAddressOperation()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Адреси",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                editingAddress = null
                showAddressDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Додати адресу"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (address == null) {
            Text(
                text = "У вас ще немає збереженої адреси",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            AddressCard(
                address = address,
                onEditClick = {
                    editingAddress = address
                    showAddressDialog = true
                }
            )
        }
    }

    if (showAddressDialog) {
        Text("Редагування або додавання адреси (в розробці)")
//        AddressDialog(
//            address = editingAddress,
//            onDismiss = { showAddressDialog = false },
//            onSave = { addressRequest ->
//                if (editingAddress != null) {
//                    TODO()
////                    viewModel.updateAddress(editingAddress!!.id, addressRequest)
//                } else {
//                    TODO()
////                    viewModel.addAddress(addressRequest)
//                }
//            },
//            isLoading = addressOperationState is Resource.Loading,
//            errorMessage = if (addressOperationState is Resource.Error)
//                (addressOperationState as Resource.Error).message else null
//        )
    }
}

@Composable
private fun AddressCard(
    address: AddressItem,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${address.region}, ${address.city}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${address.street}, буд. ${address.house}" +
                            if (address.apartment.isNotBlank()) ", кв. ${address.apartment}" else "",
                        fontSize = 14.sp
                    )

                    if (address.area.isNotBlank()) {
                        Text(
                            text = "Район: ${address.area}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редагувати адресу"
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressDialog(
    address: AddressItem?,
    onDismiss: () -> Unit,
    onSave: (AddressRequest) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    val isEditing = address != null

    var region by remember { mutableStateOf(address?.region ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var area by remember { mutableStateOf(address?.area ?: "") }
    var street by remember { mutableStateOf(address?.street ?: "") }
    var house by remember { mutableStateOf(address?.house ?: "") }
    var apartment by remember { mutableStateOf(address?.apartment ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEditing) "Редагувати адресу" else "Додати нову адресу")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Область") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Місто") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Район (необов'язково)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Вулиця") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = house,
                        onValueChange = { house = it },
                        label = { Text("Будинок") },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = apartment,
                        onValueChange = { apartment = it },
                        label = { Text("Квартира") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val addressRequest = AddressRequest(
                        region = region.trim(),
                        city = city.trim(),
                        area = area.trim(),
                        street = street.trim(),
                        house = house.trim(),
                        apartment = apartment.trim()
                    )
                    onSave(addressRequest)
                },
                enabled = !isLoading && region.isNotBlank() && city.isNotBlank() &&
                    street.isNotBlank() && house.isNotBlank()
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Composable
fun UnauthenticatedUserContent(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    scrollState: ScrollState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(bottom = 100.dp)
            )

            Text(
                text = stringResource(id = R.string.login_to_your_account),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Щоб отримати доступ до всіх функцій, увійдіть або створіть обліковий запис",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.login))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.register))
            }
        }
    }
}
