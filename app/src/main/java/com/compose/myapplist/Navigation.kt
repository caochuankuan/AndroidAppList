package com.compose.myapplist

import androidx.compose.runtime.Composable
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
                },
                onAppListBtn2Click = {
                    navController.navigate("AppListPage2")
                },
                onAppListBtn3Click = {
                    navController.navigate("AppListPage3")
                },
                onAppListBtn4Click = {
                    navController.navigate("AppListPage4")
                }
            )
        }

        composable("AppListPage1") {
            AppListScreen1(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("AppListPage2") {
            AppListScreen2(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("AppListPage3") {
            AppListScreen3(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("AppListPage4") {
            AppListScreen4(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}