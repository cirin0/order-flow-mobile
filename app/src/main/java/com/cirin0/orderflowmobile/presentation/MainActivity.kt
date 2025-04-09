package com.cirin0.orderflowmobile.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.cirin0.orderflowmobile.presentation.navigation.MainScreen
import com.cirin0.orderflowmobile.presentation.navigation.MainViewModel
import com.cirin0.orderflowmobile.presentation.ui.theme.OrderFlowMobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrderFlowMobileTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val mainViewModel: MainViewModel = hiltViewModel()
//                    MainScreen(
//                        isAuthenticated = mainViewModel.isAuthenticated.value,
//                        mainViewModel = mainViewModel,
//                    )
//                }
                val mainViewModel: MainViewModel = hiltViewModel()
                MainScreen(
                    isAuthenticated = mainViewModel.isAuthenticated.value,
                    mainViewModel = mainViewModel,
                )
            }
        }
    }
}