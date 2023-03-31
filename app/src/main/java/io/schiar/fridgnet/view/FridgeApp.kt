package io.schiar.fridgnet.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.schiar.fridgnet.view.screen.HomeScreen
import io.schiar.fridgnet.view.screen.NextScreen

@Composable
fun FridgeApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "Home") {
        composable(route = "Home") { HomeScreen { navController.navigate("Next") } }
        composable(route = "Next") { NextScreen() }
    }
}