package com.example.healthybet

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthybet.screens.ConnexionScreen
import com.example.healthybet.screens.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val SIGNUP = "signup"
    const val LOGIN = "login"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                onLogInClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.SIGNUP) {
            ConnexionScreen()
        }

        composable(Routes.LOGIN) {
            ConnexionScreen()
        }
    }
}