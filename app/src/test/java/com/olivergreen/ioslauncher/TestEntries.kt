package com.olivergreen.ioslauncher

import android.content.Intent
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.iconCacheKey

fun testEntry(label: String, pkg: String, cls: String = "MainActivity", system: Boolean = false): AppEntry =
    AppEntry(
        label = label,
        packageName = pkg,
        className = cls,
        isSystem = system,
        launchIntent = Intent(),
        iconKey = iconCacheKey(pkg, cls),
    )
