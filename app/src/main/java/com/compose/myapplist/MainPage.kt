package com.compose.myapplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
                title = { Text("获取应用列表") }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SchemeItem(
                    title = "方案一",
                    description = "用 <queries> 声明 Launcher-Intent 可见性，通过 queryIntentActivities(..., CATEGORY_LAUNCHER) 获取桌面级应用包名列表，无需任何权限。\n注意：该方案如果不加queries只能获取极少量应用",
                    buttonText = "点击查看 方案一",
                    onClick = onAppListBtn1Click
                )
            }
            item {
                SchemeItem(
                    title = "方案二",
                    description = "<uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\" /> 权限 + getInstalledPackages()",
                    buttonText = "点击查看 方案二",
                    onClick = onAppListBtn2Click
                )
            }
            item {
                SchemeItem(
                    title = "方案三",
                    description = "<uses-permission android:name=\"android.permission.QUERY_ALL_PACKAGES\" /> 权限 + queryIntentActivities()\n注意：该方案如果不加QUERY_ALL_PACKAGES权限只能获取极少量应用",
                    buttonText = "点击查看 方案三",
                    onClick = onAppListBtn3Click
                )
            }
            item {
                SchemeItem(
                    title = "方案四",
                    description = "GET_INSTALLED_APPS + QUERY_ALL_PACKAGES 权限 + getInstalledPackages() + rememberLauncherForActivityResult().launch()",
                    buttonText = "点击查看 方案四",
                    onClick = onAppListBtn4Click
                )
            }
        }
    }
}

@Composable
private fun SchemeItem(
    title: String,
    description: String,
    buttonText: String = "点击查看",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick) {
                Text(buttonText)
            }
        }
    }
}