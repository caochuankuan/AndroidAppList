package com.compose.myapplist.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

object IconSwitcher {

    enum class Icon(val aliasSuffix: String) {
        TAOBAO("AliasTaobao"),
        WEIXIN("AliasWeixin"),
        ZHIHU("AliasZhihu"),
        GAODE("AliasGaode"),
        DEEPSEEK("AliasDeepseek"),
        DOUBAO("AliasDoubao"),
        DOUYIN("AliasDouyin"),
        MIAOQU("AliasMiaoqu")
    }

    fun switchToIcon(context: Context, icon: Icon) {
        val pm = context.packageManager
        val pkg = context.packageName
        val chosen = ComponentName(pkg, "$pkg.${icon.aliasSuffix}")

        // 启用选中的别名入口
        pm.setComponentEnabledSetting(
            chosen,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // 禁用当前应用的其他 LAUNCHER 入口（避免桌面出现多个图标）
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = pm.queryIntentActivities(intent, 0)
        for (ri in resolved) {
            if (ri.activityInfo.packageName == pkg) {
                val cn = ComponentName(pkg, ri.activityInfo.name)
                if (cn != chosen) {
                    pm.setComponentEnabledSetting(
                        cn,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }

    fun restoreDefaultIcon(context: Context) {
        val pm = context.packageManager
        val pkg = context.packageName
        val defaultMain = ComponentName(pkg, "$pkg.MainActivity")

        pm.setComponentEnabledSetting(
            defaultMain,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = pm.queryIntentActivities(intent, 0)
        for (ri in resolved) {
            if (ri.activityInfo.packageName == pkg) {
                val cn = ComponentName(pkg, ri.activityInfo.name)
                if (cn != defaultMain) {
                    pm.setComponentEnabledSetting(
                        cn,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }
}