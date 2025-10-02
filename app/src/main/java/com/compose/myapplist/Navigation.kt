package com.compose.myapplist

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = "MainPage"
    ) {
        composable("MainPage") {
            MainPage(
                onAppListBtn1Click = {
                    navController.navigate("AppListPage1")
                }
            )
        }

        composable("AppListPage1") {
            AppListScreen(
                pm = LocalContext.current.packageManager,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}