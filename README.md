# Android 零权限获取桌面级应用列表方案

此处只介绍方案一，剩下三种方案参考代码AppListScreen2-3-4

## 1. 背景

1. **QUERY_ALL_PACKAGES 权限方案**：Google Play 列入敏感权限，普通场景直接拒审；国内商店也需视频+用途说明。
2. **反射调用 getInstalledPackages**：Android 12+ 彻底黑名单，抛异常+TargetSdk≥34 无法绕过，上架即崩溃。
3. **手动在 `<queries>` 枚举 `<package>` 名单**：维护成本高，新增 App 需发版。
4. **用 `<queries>` 声明 Launcher-Intent 可见性**：通过 `queryIntentActivities(...,CATEGORY_LAUNCHER)` 获取桌面级应用包名列表，无需任何权限，Google Play / 国内商店零审核。

## 2. 步骤

### 2.1 AndroidManifest.xml 声明只对桌面图标可见

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <queries>
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
    </queries>

    <application
        ......
    </application>

</manifest>
```

### 2.2 获取列表

```kotlin
val pm = context.packageManager
val main = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
val apps = pm.queryIntentActivities(main, PackageManager.MATCH_ALL)
```

## 3. 版本差异

1. **Android 版本 ≤ 10（API ≤ 29）**：`<queries>` 被忽略，直接返回桌面图标列表，无影响。
2. **Android 版本 ≥ 11（API ≥ 30）**：必须加 `<queries>` 才能拿到第三方 App。

## 4. 示例代码

### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <queries>
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
    </queries>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyAppList"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.MyAppList">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### MainActivity.kt

```kotlin
package com.compose.myapplist

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.compose.myapplist.ui.theme.MyAppListTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*
import androidx.core.graphics.createBitmap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MyAppListTheme { AppListScreen(packageManager) } }
    }
}

@Composable
fun AppListScreen(pm: PackageManager) {
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

    Scaffold { padding ->
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
        (app.icon as? android.graphics.drawable.BitmapDrawable)?.bitmap
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
            Text("APK 大小: ${android.text.format.Formatter.formatFileSize(context, app.apkSize)}")
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

            val apkFile = java.io.File(appInfo.sourceDir)
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

private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable): android.graphics.Bitmap {
    if (drawable is android.graphics.drawable.BitmapDrawable && drawable.bitmap != null) return drawable.bitmap
    val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
    val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
    val bmp = createBitmap(w, h)
    val canvas = android.graphics.Canvas(bmp)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bmp
}

data class AppInfo(
    val name: String,
    val icon: android.graphics.drawable.Drawable,
    val packageName: String,
    val installTime: Long,
    val updateTime: Long,
    val apkSize: Long
)
```