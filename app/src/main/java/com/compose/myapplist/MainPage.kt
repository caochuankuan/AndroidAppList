package com.compose.myapplist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onAppListBtn1Click: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("获取应用列表")
                }
            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("方案一：用 <queries> 声明 Launcher-Intent 可见性，通过 queryIntentActivities(...,CATEGORY_LAUNCHER) 获取桌面级应用包名列表，无需任何权限")
            Button(
                onClick = {
                    onAppListBtn1Click()
                }
            ) {
                Text("点击查看")
            }
        }
    }
}