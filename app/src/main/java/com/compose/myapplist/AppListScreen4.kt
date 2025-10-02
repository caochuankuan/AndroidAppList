package com.compose.myapplist

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen4(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val miuiPermission = "com.android.permission.GET_INSTALLED_APPS"
    val appListPermission = "android.permission.QUERY_ALL_PACKAGES"

    var isCheckingPerm by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var packages by remember { mutableStateOf(listOf<String>()) }
    var hasMiuiPermission by remember { mutableStateOf<Boolean?>(null) }
    var hasQueryAllPermission by remember { mutableStateOf<Boolean?>(null) }

    val permissionLauncherMiui = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMiuiPermission = granted
    }

    val permissionLauncherQueryAll = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasQueryAllPermission = granted
    }

    // 进入页面：先检查并请求 MIUI 权限；若结果为拒绝，再继续请求 QUERY_ALL_PACKAGES
    LaunchedEffect(Unit) {
        val miuiGranted = ContextCompat.checkSelfPermission(
            context, miuiPermission
        ) == PackageManager.PERMISSION_GRANTED
        hasMiuiPermission = miuiGranted
        if (!miuiGranted) {
            permissionLauncherMiui.launch(miuiPermission)
        }
    }

    // MIUI 权限回调后，若未授权则尝试请求 QUERY_ALL_PACKAGES
    LaunchedEffect(hasMiuiPermission) {
        if (hasMiuiPermission == null) return@LaunchedEffect
        if (hasMiuiPermission == true) {
            isCheckingPerm = false
            isLoading = true
        } else {
            val queryAllGranted = ContextCompat.checkSelfPermission(
                context, appListPermission
            ) == PackageManager.PERMISSION_GRANTED
            hasQueryAllPermission = queryAllGranted
            if (!queryAllGranted) {
                permissionLauncherQueryAll.launch(appListPermission)
            } else {
                isCheckingPerm = false
                isLoading = true
            }
        }
    }

    // 任一权限授权成功后，只做数据加载
    LaunchedEffect(hasMiuiPermission, hasQueryAllPermission) {
        val canLoad = (hasMiuiPermission == true) || (hasQueryAllPermission == true)
        if (canLoad && packages.isEmpty()) {
            isCheckingPerm = false
            isLoading = true
            packages = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                if (Build.VERSION.SDK_INT >= 33) {
                    pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
                        .map { it.packageName }
                } else {
                    @Suppress("DEPRECATION")
                    pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                        .map { it.packageName }
                }
            }
            isLoading = false
        } else if (!canLoad && hasMiuiPermission != null && hasQueryAllPermission != null) {
            isCheckingPerm = false
            isLoading = false
        }
    }
    val canLoad = (hasMiuiPermission == true) || (hasQueryAllPermission == true)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("应用列表4") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_delete),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isCheckingPerm -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("正在检查权限...", style = MaterialTheme.typography.bodyLarge)
                }
            }
            !canLoad && hasMiuiPermission != null && hasQueryAllPermission != null -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("两种权限均未授权，无法获取全量应用列表", style = MaterialTheme.typography.bodyLarge)
                }
            }
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "正在加载...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            packages.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("未获取到已安装包", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "共 ${packages.size} 个包",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    items(packages) { pkg ->
                        Text(pkg, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}