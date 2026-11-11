package com.olivergreen.ioslauncher.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageChangeReceiver : BroadcastReceiver() {

    var onChange: (() -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        onChange?.invoke()
    }
}
