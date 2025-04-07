package com.cirin0.orderflowmobile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    isAuthenticated: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAuthenticated) {
            AuthenticatedUserContent()
        } else {
            UnauthenticatedUserContent(
                onLoginClick = onNavigateToLogin,
                onRegisterClick = onNavigateToRegister
            )
        }
    }
}

@Composable
private fun AuthenticatedUserContent() {
    Text(
        text = "Мій профіль",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Profile information would go here
    Text(
        text = "Ви успішно увійшли в систему",
        fontSize = 16.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { /* Logout logic */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Вийти з облікового запису")
    }
}

@Composable
fun UnauthenticatedUserContent(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Text(
        text = "Увійдіть до свого облікового запису",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Щоб отримати доступ до всіх функцій, увійдіть або створіть обліковий запис",
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 32.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Увійти")
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedButton(
        onClick = onRegisterClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Зареєструватися")
    }
}

