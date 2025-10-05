package com.compose.myapplist.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen1(
    pm: PackageManager = LocalContext.current.packageManager,
    onBackClick: () -> Unit
) {
    var apps by remember { mutableStateOf(listOf<AppInfo>()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val dateFmt = remember { DateFormat.getDateTimeInstance() }

    LaunchedEffect(Unit) {
        scope.launch {
            apps = loadApps(pm)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("应用列表1") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_delete),
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> Loading()
                apps.isEmpty() -> Empty()
                else -> AppList(apps, dateFmt)
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("正在加载应用列表...")
        }
    }
}

@Composable
private fun Empty() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("暂无应用", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AppList(apps: List<AppInfo>, dateFmt: DateFormat) {
    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(apps) { AppRow(it, dateFmt) }
    }
}

@Composable
private fun AppRow(app: AppInfo, dateFmt: DateFormat) {
    val context = LocalContext.current
    val bmp = remember(app.icon) {
        (app.icon as? BitmapDrawable)?.bitmap
            ?: drawableToBitmap(app.icon)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(app.name, style = MaterialTheme.typography.bodyLarge)
                    Text(app.packageName, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("安装时间: ${dateFmt.format(Date(app.installTime))}")
            Text("更新时间: ${dateFmt.format(Date(app.updateTime))}")
            Text("APK 大小: ${Formatter.formatFileSize(context, app.apkSize)}")
        }
    }
}

private suspend fun loadApps(pm: PackageManager): List<AppInfo> = withContext(Dispatchers.IO) {

    val main = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfos = pm.queryIntentActivities(main, PackageManager.MATCH_ALL)

    resolveInfos
        .distinctBy { it.activityInfo.packageName }          // 去重
        .mapNotNull { ri ->                                   // 异常吃掉
            val pkg = ri.activityInfo.packageName

            val pInfo = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
                else @Suppress("DEPRECATION") pm.getPackageInfo(pkg, 0)
            }.getOrNull() ?: return@mapNotNull null

            val appInfo = runCatching {
                pm.getApplicationInfo(pkg, 0)
            }.getOrNull() ?: return@mapNotNull null

            val apkFile = File(appInfo.sourceDir)
            AppInfo(
                name = ri.loadLabel(pm).toString(),
                icon = ri.loadIcon(pm),
                packageName = pkg,
                installTime = pInfo.firstInstallTime,
                updateTime = pInfo.lastUpdateTime,
                apkSize = apkFile.length()
            )
        }
        .sortedBy { it.name.lowercase() }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) return drawable.bitmap
    val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
    val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
    val bmp = createBitmap(w, h)
    val canvas = Canvas(bmp)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bmp
}

data class AppInfo(
    val name: String,
    val icon: Drawable,
    val packageName: String,
    val installTime: Long,
    val updateTime: Long,
    val apkSize: Long
)