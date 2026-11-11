package com.olivergreen.ioslauncher.data

import android.content.Intent
import androidx.compose.runtime.Immutable

@Immutable
data class AppEntry(
    val label: String,
    val packageName: String,
    val className: String,
    val isSystem: Boolean,
    val launchIntent: Intent,
    val iconKey: String,
)
