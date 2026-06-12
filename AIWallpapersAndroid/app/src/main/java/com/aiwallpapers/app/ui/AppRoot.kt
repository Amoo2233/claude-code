package com.aiwallpapers.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aiwallpapers.app.billing.PremiumManager
import com.aiwallpapers.app.data.FavoritesStore
import com.aiwallpapers.app.data.WallpaperActions
import com.aiwallpapers.app.data.WallpaperCatalog

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("browse", "Browse", Icons.Default.Photo),
    Tab("categories", "Categories", Icons.Default.GridView),
    Tab("favorites", "Favorites", Icons.Default.Favorite),
    Tab("settings", "Settings", Icons.Default.Settings),
)

@Composable
fun AppRoot(
    catalog: WallpaperCatalog,
    premium: PremiumManager,
    favorites: FavoritesStore,
    actions: WallpaperActions,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (tabs.any { it.route == currentRoute }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "browse",
            modifier = Modifier.padding(padding),
        ) {
            composable("browse") {
                BrowseScreen(catalog, premium, navController)
            }
            composable("categories") {
                CategoriesScreen(catalog, navController)
            }
            composable("category/{name}") { entry ->
                val name = entry.arguments?.getString("name").orEmpty()
                CategoryScreen(name, catalog, favorites, navController)
            }
            composable("favorites") {
                FavoritesScreen(catalog, favorites, navController)
            }
            composable("settings") {
                SettingsScreen(catalog, premium, navController)
            }
            composable("wallpaper/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                catalog.byId(id)?.let { wallpaper ->
                    DetailScreen(wallpaper, premium, favorites, actions, navController)
                }
            }
            composable("paywall") {
                PaywallScreen(premium, navController)
            }
        }
    }
}
