package com.example.feature.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature.model.NewUiModel
import com.example.feature.ui.detail.DetailScreen
import com.example.feature.ui.home.HomeScreen
import com.example.feature.ui.login.LoginScreen

@Composable
fun AppNavHost(
    finishApp: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onClose = { finishApp() },
                onNavigate = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen { newUiModel ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("newUiModel", newUiModel)
                navController.navigate(Routes.DETAILS)
            }
        }

        composable(Routes.DETAILS) {
            val newUiModel =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<NewUiModel>("newUiModel")

            newUiModel?.let {
                DetailScreen(
                    newUiModel = it,
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }
    }
}
