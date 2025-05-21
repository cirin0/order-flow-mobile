package com.cirin0.orderflowmobile.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cirin0.orderflowmobile.R
import com.cirin0.orderflowmobile.domain.model.Category
import com.cirin0.orderflowmobile.domain.model.Product
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay


data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val bottomNavItems = createBottomNavItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    LaunchedEffect(currentDestination) {
        if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
            viewModel.clearSearchResults()
        }
    }

    LaunchedEffect(isSearchActive) {
        if (!isSearchActive && searchState.searchResult != null) {
            searchQuery = ""
            viewModel.clearSearchResults()
        }
    }

    LaunchedEffect(searchQuery, isSearchActive) {
        if (isSearchActive && searchQuery.isNotEmpty()) {
            delay(500)
            viewModel.search(searchQuery)
        }
    }

    val navigateFromSearch: (String) -> Unit = { route ->
        isSearchActive = false
        searchQuery = ""
        viewModel.clearSearchResults()
        navController.navigate(route)
    }

    val routesWithoutSearchBar = listOf(
        NavRoutes.PROFILE,
        NavRoutes.LOGIN,
        NavRoutes.REGISTER,
        NavRoutes.CART,
        NavRoutes.FAVORITES,
        NavRoutes.CATEGORY,
        NavRoutes.PASSWORD_RESET,
        NavRoutes.USER_ORDERS,
        NavRoutes.ORDER,
        NavRoutes.ORDER_DETAILS
    )

    val shouldShowSearchBar = when {
        currentRoute == null -> true
        currentRoute in routesWithoutSearchBar -> false
        currentRoute.startsWith("${NavRoutes.PRODUCT}/") -> false
        currentRoute.startsWith("${NavRoutes.CATEGORY}/") -> false
        currentRoute.startsWith("${NavRoutes.ORDER_DETAILS}/") -> false
        else -> true
    }

    val showBottomNav = when {
        currentDestination?.route == null -> false
        currentDestination.route in listOf(
            NavRoutes.HOME,
            NavRoutes.FAVORITES,
            NavRoutes.CART,
            NavRoutes.PROFILE,
            NavRoutes.LOGIN,
            NavRoutes.REGISTER,
            NavRoutes.PASSWORD_RESET,
            NavRoutes.USER_ORDERS,
            NavRoutes.ORDER,
            NavRoutes.ORDER_DETAILS
        ) -> true

        currentDestination.route?.startsWith("${NavRoutes.CATEGORY}/") == true -> true
        currentDestination.route?.startsWith("${NavRoutes.PRODUCT}/") == true -> true
        currentDestination.route?.startsWith("${NavRoutes.ORDER_DETAILS}/") == true -> true
        else -> false
    }

    Scaffold(
        topBar = {
            if (shouldShowSearchBar) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                    )
                    SearchBar(
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
                        trailingIcon = {
                            if (isSearchActive) {
                                IconButton(onClick = {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearchResults()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Закрити"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        windowInsets = WindowInsets(top = 0.dp),
                        shape = RoundedCornerShape(16.dp),
                        content = {
                            when {
                                searchState.isLoading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                searchState.error.isNotEmpty() -> {
                                    Text(
                                        text = searchState.error,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                    )
                                }

                                searchState.searchResult != null -> {
                                    val products = searchState.searchResult?.products.orEmpty()
                                    val categories = searchState.searchResult?.categories.orEmpty()

                                    if (products.isEmpty() && categories.isEmpty()) {
                                        Text(
                                            text = "Нічого не знайдено",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 24.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        LazyColumn {
                                            if (categories.isNotEmpty()) {
                                                item {
                                                    Text(
                                                        text = "Категорії",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                                items(categories) { category ->
                                                    SearchCategoryItem(
                                                        category = category,
                                                        onClick = {
                                                            navigateFromSearch("${NavRoutes.CATEGORY}/${category.name}")
                                                        }
                                                    )
                                                }
                                            }

                                            if (products.isNotEmpty()) {
                                                item {
                                                    Text(
                                                        text = "Товари",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                                items(products) { product ->
                                                    SearchProductItem(
                                                        product = product,
                                                        onClick = {
                                                            navigateFromSearch("${NavRoutes.PRODUCT}/${product.id}")
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
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
                                val isResetNeeded = item.route == NavRoutes.HOME &&
                                    (currentDestination?.route?.startsWith("${NavRoutes.PRODUCT}/") == true ||
                                        currentDestination?.route?.startsWith("${NavRoutes.CATEGORY}/") == true ||
                                        currentDestination?.route == NavRoutes.CART ||
                                        currentDestination?.route == NavRoutes.LOGIN)

                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = !isResetNeeded
                                    }
                                    launchSingleTop = true
                                    restoreState = !isResetNeeded
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
            )
        }
    }
}

@Composable
private fun createBottomNavItems() = remember {
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
            unselectedIcon = {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = "Улюблене"
                )
            }
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

@Composable
fun SearchCategoryItem(
    category: Category,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(category.name) },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        },
        modifier = Modifier
            .clickable(onClick = onClick)
    )
}

@Composable
fun SearchProductItem(
    product: Product,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(product.name) },
        supportingContent = { Text("${product.price} грн") },
        leadingContent = {
            GlideImage(
                imageModel = { product.imageUrl },
                modifier = Modifier.size(40.dp),
                loading = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                },
                failure = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                }
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
