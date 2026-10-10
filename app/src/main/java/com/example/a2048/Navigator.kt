package com.example.a2048

import androidx.compose.runtime.Composable
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
            composable(Routes.GAME) {
                GameLogicWithViewModel(
                    onOpenRecords = { navController.navigate(Routes.RECORDS) }
                )
            }

            composable(Routes.RECORDS) {
                RecordsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

object Routes {
    const val GAME = "game"
    const val RECORDS = "records"
}
