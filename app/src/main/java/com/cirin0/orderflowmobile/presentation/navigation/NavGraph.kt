package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cirin0.orderflowmobile.presentation.screen.CartScreen
import com.cirin0.orderflowmobile.presentation.screen.FavoritesScreen
import com.cirin0.orderflowmobile.presentation.screen.HomeScreen
import com.cirin0.orderflowmobile.presentation.screen.LoginScreen
import com.cirin0.orderflowmobile.presentation.screen.ProductScreen
import com.cirin0.orderflowmobile.presentation.screen.ProfileScreen
import com.cirin0.orderflowmobile.presentation.screen.RegisterScreen
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.ProductViewModel
import com.cirin0.orderflowmobile.presentation.screen.viewmodel.RegisterViewModel

object NavRoutes {
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val CART = "cart"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavRoutes.HOME,
    isAuthenticated: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(NavRoutes.FAVORITES) {
            FavoritesScreen()
        }
        composable(NavRoutes.CART) {
            CartScreen()
        }
        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                isAuthenticated = isAuthenticated,
                onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.PROFILE)
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                navController = navController,
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.PROFILE)
                }
            )
        }
        composable(
            route = "product/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val productViewModel: ProductViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("id")
            ProductScreen(
                viewModel = productViewModel,
                navController = navController,
                id = id
            )
        }
    }
}