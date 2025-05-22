package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cirin0.orderflowmobile.R
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.RegisterState
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.RegisterViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.StyledButton


@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
) {

    val state = viewModel.state.value
    var passwordVisibility by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    RegisterView(
        scrollState = scrollState,
        passwordVisibility = passwordVisibility,
        onPasswordVisibilityChange = { passwordVisibility = it },
        viewModel = viewModel,
        state = state,
        onLoginClick = {
            onNavigateToLogin()
        }
    )
}

@Composable
fun RegisterView(
    scrollState: ScrollState,
    passwordVisibility: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    viewModel: RegisterViewModel,
    state: RegisterState,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
//                .padding(top = 150.dp)
        )
        Text(
            text = stringResource(id = R.string.register_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 75.dp)
                .padding(bottom = 25.dp),
        )
        Row(
            modifier = Modifier
                .padding(bottom = 15.dp)
        ) {
            OutlinedTextField(
                value = viewModel.firstName.value,
                onValueChange = viewModel::onFirstNameChanged,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(id = R.string.enter_first_name)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "First Name Icon"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                isError = !viewModel.isFirstNameValid.value && viewModel.firstName.value.isNotEmpty(),
                supportingText = {
                    if (!viewModel.isFirstNameValid.value && viewModel.firstName.value.isNotEmpty()) {
                        Text(
                            text = "Введіть коректне ім'я",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.padding(4.dp))

            OutlinedTextField(
                value = viewModel.lastName.value,
                onValueChange = viewModel::onLastNameChanged,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(id = R.string.enter_last_name)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Last Name Icon"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                isError = !viewModel.isLastNameValid.value && viewModel.lastName.value.isNotEmpty(),
                supportingText = {
                    if (!viewModel.isLastNameValid.value && viewModel.lastName.value.isNotEmpty()) {
                        Text(
                            text = "Введіть коректне прізвище",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = viewModel::onEmailChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            label = { Text(stringResource(id = R.string.enter_email)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(8.dp),
            isError = !viewModel.isEmailValid.value && viewModel.email.value.isNotEmpty(),
            supportingText = {
                if (!viewModel.isEmailValid.value && viewModel.email.value.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.invalid_email),
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        )
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.enter_password)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password Icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisibility) }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisibility) "Hide Password" else "Show Password"
                    )
                }
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(8.dp),
            isError = !viewModel.isPasswordValid.value && viewModel.password.value.isNotEmpty(),
            supportingText = {
                if (!viewModel.isPasswordValid.value && viewModel.password.value.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.invalid_password),
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        )
        StyledButton(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth(),
            onClick = { viewModel.register() },
            content = {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.register),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            enabled = !state.isLoading
                && viewModel.email.value.isNotEmpty()
                && viewModel.password.value.isNotEmpty()
                && viewModel.firstName.value.isNotEmpty()
                && viewModel.lastName.value.isNotEmpty()
                && viewModel.isEmailValid.value
                && viewModel.isPasswordValid.value
                && viewModel.isFirstNameValid.value
                && viewModel.isLastNameValid.value,
        )
        Text(
            text = stringResource(id = R.string.already_registered),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 15.dp)
                .clickable(
                    onClick = onLoginClick,
                )
        )
    }
}
