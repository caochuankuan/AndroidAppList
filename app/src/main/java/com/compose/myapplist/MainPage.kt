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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.compose.myapplist.utils.IconSwitcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onAppListBtn1Click: () -> Unit,
    onAppListBtn2Click: () -> Unit,
    onAppListBtn3Click: () -> Unit,
    onAppListBtn4Click: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTab = remember { mutableIntStateOf(0) }
    val tabs = listOf("列表", "Icon")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (selectedTab.intValue == 0) "获取应用列表" else "图标切换") }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab.intValue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab.intValue == index,
                        onClick = { selectedTab.intValue = index },
                        text = { Text(title) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            if (selectedTab.intValue == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
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
            } else {
                Column(Modifier.fillMaxSize()) {
                    Text("选择一个图标预览并点击切换", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        IconPreviewRow { icon ->
                            IconSwitcher.switchToIcon(context, icon)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            IconSwitcher.restoreDefaultIcon(context)
                            android.widget.Toast.makeText(
                                context,
                                "已恢复默认图标",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("恢复默认图标")
                    }
                }
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

@Composable
private fun IconPreviewRow(
    onIconClick: (IconSwitcher.Icon) -> Unit
) {
    val icons = listOf(
        IconSwitcher.Icon.TAOBAO to R.drawable.taobao,
        IconSwitcher.Icon.WEIXIN to R.drawable.weixin,
        IconSwitcher.Icon.ZHIHU to R.drawable.zhihu,
        IconSwitcher.Icon.GAODE to R.drawable.gaode,
        IconSwitcher.Icon.DEEPSEEK to R.drawable.deepseek,
        IconSwitcher.Icon.DOUBAO to R.drawable.doubao,
        IconSwitcher.Icon.DOUYIN to R.drawable.douyin,
        IconSwitcher.Icon.MIAOQU to R.drawable.miaoqu,
    )

    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(icons.size) { index ->
            val (icon, resId) = icons[index]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = icon.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            onIconClick(icon)
                            android.widget.Toast.makeText(
                                context,
                                "已切换为 ${icon.name}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                )
                Spacer(Modifier.height(6.dp))
                Text(icon.name)
            }
        }
    }
}