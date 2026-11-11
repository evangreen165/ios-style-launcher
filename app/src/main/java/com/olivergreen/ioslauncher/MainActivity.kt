package com.olivergreen.ioslauncher

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.olivergreen.ioslauncher.ui.HomeScreen
import com.olivergreen.ioslauncher.ui.theme.IOSLauncherTheme
import com.olivergreen.ioslauncher.util.PackageChangeReceiver
import com.olivergreen.ioslauncher.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private val packageReceiver = PackageChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        packageReceiver.onChange = { viewModel.reload() }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(this, packageReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        setContent {
            IOSLauncherTheme {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {},
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshWallpaper()
    }

    override fun onDestroy() {
        packageReceiver.onChange = null
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }
}
