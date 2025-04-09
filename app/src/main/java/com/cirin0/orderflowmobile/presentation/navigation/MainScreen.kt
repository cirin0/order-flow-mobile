package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isAuthenticated: Boolean = false,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel? = null,
) {
    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                route = NavRoutes.HOME,
                title = "Головна",
                selectedIcon = { Icon(Icons.Filled.Home, contentDescription = "Головна") },
                unselectedIcon = { Icon(Icons.Outlined.Home, contentDescription = "Головна") }
            ),
            BottomNavItem(
                route = NavRoutes.FAVORITES,
                title = "Улюблене",
                selectedIcon = { Icon(Icons.Filled.Favorite, contentDescription = "Улюблене") },
                unselectedIcon = { Icon(Icons.Outlined.Favorite, contentDescription = "Улюблене") }
            ),
            BottomNavItem(
                route = NavRoutes.CART,
                title = "Кошик",
                selectedIcon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Кошик") },
                unselectedIcon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = "Кошик") }
            ),
            BottomNavItem(
                route = NavRoutes.PROFILE,
                title = "Профіль",
                selectedIcon = { Icon(Icons.Filled.Person, contentDescription = "Профіль") },
                unselectedIcon = { Icon(Icons.Outlined.Person, contentDescription = "Профіль") }
            )
        )
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        if (isSearchActive)
            isSearchActive = false
    }

    Scaffold(
        topBar = {
            DockedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Пошук товарів...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Пошук"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                content = {
                    if (searchQuery.isNotEmpty()) {
                        val items = listOf("Товар 1", "Товар 2", "Товар 3")
                            .filter { it.contains(searchQuery, ignoreCase = true) }
                        items.forEach { item ->
                            ListItem(
                                headlineContent = { Text(item) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable {
                                    searchQuery = item
                                    isSearchActive = false
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showBottomNav = when (currentDestination?.route) {
                null -> false
                in listOf(
                    NavRoutes.HOME,
                    NavRoutes.FAVORITES,
                    NavRoutes.CART,
                    NavRoutes.PROFILE,
                    NavRoutes.LOGIN,
                    NavRoutes.REGISTER,
                ) -> true

                else -> currentDestination.route?.startsWith("product/") == true
            }

            if (showBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = {
                                if (selected) {
                                    item.selectedIcon()
                                } else {
                                    item.unselectedIcon()
                                }
                            },
                            label = { Text(item.title) },
                            alwaysShowLabel = true,
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Clear the back stack to avoid navigating back to the previous screen
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    enabled = isSearchActive,
                    onClick = { isSearchActive = false },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            AppNavHost(
                navController = navController,
                isAuthenticated = isAuthenticated
            )
        }
    }
}