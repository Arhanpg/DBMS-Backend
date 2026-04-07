package com.example.dbmstool.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dbmstool.ui.screens.*
import com.example.dbmstool.viewmodel.MainViewModel

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, "home"),
    BottomNavItem("Tables", Icons.Default.TableChart, "tables"),
    BottomNavItem("Queries", Icons.Default.Search, "queries"),
    BottomNavItem("Custom", Icons.Default.Code, "custom")
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("tables") { TablesScreen(viewModel) }
            composable("queries") { QueriesScreen(viewModel) }
            composable("add_user") {
                AddUserScreen(
                    onSave = { data ->

                        if (data.isNotEmpty()) {
                            // TODO: save to database later
                            println("Saved: $data")
                        }

                        // go back in BOTH cases (save or cancel)
                        navController.popBackStack()
                    }
                )
            }
            composable("custom") {
                CustomQueryScreen(
                    viewModel = viewModel,
                    onAddUserClick = {
                        navController.navigate("add_user")
                    }
                )
            }
        }
    }
}