package com.cirin0.orderflowmobile.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
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
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.LoginState
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.LoginViewModel
import com.cirin0.orderflowmobile.presentation.ui.component.StyledButton


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
) {
    val state = viewModel.state.value
    var passwordVisibility by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(key1 = state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(message = state.error)
        }
    }

    LoginView(
        scrollState = scrollState,
        passwordVisibility = passwordVisibility,
        onPasswordVisibilityChange = { passwordVisibility = it },
        viewModel = viewModel,
        state = state,
        onRegisterClick = {
            onNavigateToRegister()
        },
        onPasswordResetClick = {
            onNavigateToPasswordReset()
        }
    )
}

@Composable
fun LoginView(
    scrollState: ScrollState,
    passwordVisibility: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    viewModel: LoginViewModel,
    state: LoginState,
    onRegisterClick: () -> Unit,
    onPasswordResetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
//                .padding(top = 150.dp)
        )
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 75.dp)
                .padding(bottom = 25.dp),
        )
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
            shape = RoundedCornerShape(8.dp)
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
            shape = RoundedCornerShape(8.dp)
        )
        StyledButton(
            modifier = Modifier
                .padding(vertical = 25.dp)
                .fillMaxWidth(),
            onClick = { viewModel.login() },
            content = {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            },
            enabled = !state.isLoading && viewModel.email.value.isNotEmpty() && viewModel.password.value.isNotEmpty()
        )
        Text(
            text = stringResource(id = R.string.not_registered),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable(
                    onClick = onRegisterClick,
                )
        )
        Text(
            text = stringResource(id = R.string.forgot_password),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 20.dp)
                .clickable(
                    onClick = onPasswordResetClick,
                )
        )
    }
}
