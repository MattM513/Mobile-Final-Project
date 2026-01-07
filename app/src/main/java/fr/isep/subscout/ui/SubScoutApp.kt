package fr.isep.subscout.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isep.subscout.ui.add.AddSubscriptionScreen
import fr.isep.subscout.ui.admin.AdminDashboardScreen
import fr.isep.subscout.ui.auth.LoginScreen
import fr.isep.subscout.ui.auth.SignUpScreen
import fr.isep.subscout.ui.home.HomeScreen
import fr.isep.subscout.ui.theme.SubscoutTheme

@Composable
fun SubScoutApp() {
    SubscoutTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                    onNavigateToSignUp = { navController.navigate("signup") }
                )
            }
            
            composable("signup") {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("home") {
                HomeScreen(
                    onAddClick = { navController.navigate("add") },
                    onAdminClick = { navController.navigate("admin") },
                    onLogout = { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                    onEditClick = { subId -> navController.navigate("edit/$subId") }
                )
            }
            
            composable("admin") {
                AdminDashboardScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("add") {
                AddSubscriptionScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "edit/{subId}"
            ) { backStackEntry ->
                val subId = backStackEntry.arguments?.getString("subId") ?: return@composable
                fr.isep.subscout.ui.edit.EditSubscriptionScreen(
                    subscriptionId = subId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
