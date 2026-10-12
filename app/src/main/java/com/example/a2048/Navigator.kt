package com.example.a2048

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Navigator {

    @Composable
    fun AppRoot() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Routes.GAME,
        ) {
            composable(Routes.GAME) { entry ->
                GameLogicWithViewModel(
                    onOpenRecords = {
                        if (navController.currentBackStackEntry == entry &&
                            entry.lifecycle.currentState == Lifecycle.State.RESUMED
                        ) {
                            navController.navigate(Routes.RECORDS) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Routes.RECORDS) { entry ->
                RecordsRoute(
                    onBack = {
                        if (navController.currentBackStackEntry == entry &&
                            entry.lifecycle.currentState == Lifecycle.State.RESUMED
                        ) {
                            navController.popBackStack(Routes.GAME, inclusive = false)
                        }
                    }
                )
            }
        }
    }
}

object Routes {
    const val GAME = "game"
    const val RECORDS = "records"
}
