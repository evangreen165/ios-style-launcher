package com.olivergreen.ioslauncher.util

import android.content.Intent
import android.net.Uri
import android.provider.Settings

object LauncherIntents {

    fun appInfo(packageName: String): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))

    fun delete(packageName: String): Intent =
        Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
}
