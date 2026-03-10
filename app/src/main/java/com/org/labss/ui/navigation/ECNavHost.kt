package com.org.labss.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.org.labss.ui.features.home.HomeScreen
import com.org.labss.ui.features.search.SearchResultsScreen
import com.org.labss.ui.vm.ProductEvent
import com.org.labss.ui.vm.SharedViewModel

@Composable
fun ECNavHost(viewModel: SharedViewModel) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    // Слідкуємо за поточним маршрутом
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Коли повертаємось на Home — очищуємо пошук
    LaunchedEffect(currentRoute) {
        if (currentRoute == Routes.Home.route) {
            viewModel.onEvent(ProductEvent.OnSearchQueryChanged(""))
            viewModel.onEvent(ProductEvent.OnCategorySelected(null))
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            LaunchedEffect(Unit) {
                viewModel.resetSearch()
            }
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToSearch = { query, category ->
                    navController.navigate(Routes.Search.create(query, category))
                }
            )
        }

        composable(
            route = Routes.Search.route,
            arguments = listOf(
                navArgument("query") { type = NavType.StringType; defaultValue = "" },
                navArgument("category") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query").orEmpty()
            val categoryArg = backStackEntry.arguments?.getString("category").orEmpty()
            val category = categoryArg.ifBlank { null }

            LaunchedEffect(Unit) {
                if (!viewModel.isSearchInitialized) {
                    viewModel.isSearchInitialized = true
                    viewModel.onEvent(ProductEvent.OnSearchQueryChanged(query))
                    viewModel.onEvent(ProductEvent.OnCategorySelected(category))
                }
            }

            SearchResultsScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}