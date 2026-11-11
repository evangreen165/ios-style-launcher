package com.olivergreen.ioslauncher

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.olivergreen.ioslauncher.ui.AppDrawer
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
                val apps by viewModel.apps.collectAsState()
                val icons by viewModel.icons.collectAsState()
                val showLabels by viewModel.showLabels.collectAsState()
                val wallpaper by viewModel.wallpaperData.collectAsState()
                val context = LocalContext.current
                var drawerVisible by rememberSaveable { mutableStateOf(false) }

                Box(Modifier.fillMaxSize()) {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { drawerVisible = true },
                        onLongPress = null,
                    )
                    AnimatedVisibility(
                        visible = drawerVisible,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    ) {
                        AppDrawer(
                            apps = apps,
                            icons = icons,
                            showLabels = showLabels,
                            blurred = wallpaper?.blurred,
                            onOpen = { entry ->
                                context.startActivity(entry.launchIntent)
                                drawerVisible = false
                            },
                            onLongPress = null,
                            onClose = { drawerVisible = false },
                        )
                    }
                }
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
