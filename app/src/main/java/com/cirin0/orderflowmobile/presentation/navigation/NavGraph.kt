package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cirin0.orderflowmobile.presentation.screen.CartScreen
import com.cirin0.orderflowmobile.presentation.screen.CategoryScreen
import com.cirin0.orderflowmobile.presentation.screen.FavoritesScreen
import com.cirin0.orderflowmobile.presentation.screen.HomeScreen
import com.cirin0.orderflowmobile.presentation.screen.LoginScreen
import com.cirin0.orderflowmobile.presentation.screen.PasswordResetScreen
import com.cirin0.orderflowmobile.presentation.screen.ProductScreen
import com.cirin0.orderflowmobile.presentation.screen.ProfileScreen
import com.cirin0.orderflowmobile.presentation.screen.RegisterScreen

object NavRoutes {
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val CART = "cart"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PRODUCT = "product"
    const val CATEGORY = "category"
    const val PASSWORD_RESET = "password_reset"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavRoutes.HOME,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(NavRoutes.FAVORITES) {
            FavoritesScreen(
                navController = navController
            )
        }
        composable(NavRoutes.CART) {
            CartScreen(
                navController = navController,
            )
        }
        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) },
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.PROFILE)
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                onNavigateToPasswordReset = {
                    navController.navigate(NavRoutes.PASSWORD_RESET)
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.LOGIN)
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN)
                },
            )
        }
        composable(
            route = "${NavRoutes.PRODUCT}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProductScreen(
                navController = navController,
                id = id
            )
        }
        composable(
            route = "${NavRoutes.CATEGORY}/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            CategoryScreen(
                navController = navController,
                name = name
            )
        }
        composable(NavRoutes.PASSWORD_RESET) {
            PasswordResetScreen(
                onResetSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.LOGIN)
                }
            )
        }
    }
}
