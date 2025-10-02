package com.compose.myapplist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onAppListBtn1Click: () -> Unit,
    onAppListBtn2Click: () -> Unit,
    onAppListBtn3Click: () -> Unit,
    onAppListBtn4Click: () -> Unit,
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
            Text("注意：该方案如果不加queries只能获取极少量应用")
            Button(
                onClick = {
                    onAppListBtn1Click()
                }
            ) {
                Text("点击查看")
            }

            Spacer(Modifier.padding(16.dp))

            Text("方案二：<uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\" /> 权限 + getInstalledPackages()")
            Button(
                onClick = {
                    onAppListBtn2Click()
                }
            ) {
                Text("点击查看")
            }

            Spacer(Modifier.padding(16.dp))
            Text("方案三：<uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\" /> 权限 + queryIntentActivities()")
            Text("注意：该方案如果不加QUERY_ALL_PACKAGES权限只能获取极少量应用")
            Button(
                onClick = {
                    onAppListBtn3Click()
                }
            ) {
                Text("点击查看")
            }

            Spacer(Modifier.padding(16.dp))
            Text("方案四：GET_INSTALLED_APPS + QUERY_ALL_PACKAGES 权限 + getInstalledPackages() + rememberLauncherForActivityResult().launch()")
            Button(
                onClick = {
                    onAppListBtn4Click()
                }
            ) {
                Text("点击查看")
            }
        }
    }
}