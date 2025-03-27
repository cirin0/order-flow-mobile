package com.cirin0.orderflowmobile.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cirin0.orderflowmobile.presentation.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrderFlowMobileTheme {
                val context = LocalContext.current
                var isLoggedIn by remember { mutableStateOf(false) }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoggedIn) {
                        // Тут буде головний екран після авторизації
                        HomeScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                                Toast.makeText(context, "Вхід успішний!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Тимчасовий екран, доки ми не розробимо повноцінний головний екран
    Text(
        text = "Ласкаво просимо! Вхід успішний.",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OrderFlowMobileTheme {
        LoginScreen(onLoginSuccess = {})
    }
}