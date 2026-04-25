package com.example.feeder.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.feeder.feature.auth.presentation.AuthScreen
import com.example.feeder.feature.home.presentation.HomeScreen
import com.example.feeder.feature.splash.presentation.SplashScreen

@Composable
fun AppNavHost(
    navController : NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.Splash) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen()
        }

        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}