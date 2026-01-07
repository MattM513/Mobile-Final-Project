package fr.isep.subscout.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isep.subscout.ui.add.AddSubscriptionScreen
import fr.isep.subscout.ui.home.HomeScreen
import fr.isep.subscout.ui.theme.SubscoutTheme

@Composable
fun SubScoutApp() {
    SubscoutTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onAddClick = { navController.navigate("add") }
                )
            }
            composable("add") {
                AddSubscriptionScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
