package com.olivergreen.ioslauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val ownPackage: String = context.packageName
    private val collator: Collator = Collator.getInstance(Locale("ru", "RU"))

    suspend fun loadApps(): List<AppEntry> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolved.asSequence()
            .mapNotNull { info ->
                val activity = info.activityInfo ?: return@mapNotNull null
                val component = ComponentName(activity.packageName, activity.name)
                val label = activity.loadLabel(packageManager).toString()
                val flags = activity.applicationInfo?.flags ?: 0
                val isSystem = flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                AppEntry(
                    label = label,
                    packageName = activity.packageName,
                    className = activity.name,
                    isSystem = isSystem,
                    launchIntent = Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .setComponent(component)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    iconKey = iconCacheKey(activity.packageName, activity.name),
                )
            }
            .filter { it.packageName != ownPackage }
            .sortedWith(compareBy(collator) { it.label })
            .toList()
    }
}
