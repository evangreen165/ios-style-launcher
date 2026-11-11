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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.AppTheme
import com.olivergreen.ioslauncher.data.iconCacheKey
import com.olivergreen.ioslauncher.ui.AppDrawer
import com.olivergreen.ioslauncher.ui.HomeScreen
import com.olivergreen.ioslauncher.ui.components.ContextMenu
import com.olivergreen.ioslauncher.ui.components.DefaultLauncherCard
import com.olivergreen.ioslauncher.ui.components.SettingsSheet
import com.olivergreen.ioslauncher.ui.theme.IOSLauncherTheme
import com.olivergreen.ioslauncher.util.LauncherIntents
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
            val apps by viewModel.apps.collectAsState()
            val combinedApps by viewModel.combinedApps.collectAsState()
            val icons by viewModel.icons.collectAsState()
            val showLabels by viewModel.showLabels.collectAsState()
            val theme by viewModel.theme.collectAsState()
            val firstRunDone by viewModel.firstRunDone.collectAsState()
            val wallpaper by viewModel.wallpaperData.collectAsState()
            val context = LocalContext.current
            val haptic = LocalHapticFeedback.current

            var drawerVisible by rememberSaveable { mutableStateOf(false) }
            var settingsVisible by rememberSaveable { mutableStateOf(false) }
            var menuEntry by remember { mutableStateOf<AppEntry?>(null) }
            var isDefault by remember { mutableStateOf(LauncherIntents.isHomeDefault(context)) }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                isDefault = LauncherIntents.isHomeDefault(context)
                if (isDefault && !firstRunDone) {
                    viewModel.setFirstRunDone()
                }
                viewModel.refreshWallpaper()
            }

            val onOpenApp: (AppEntry) -> Unit = { entry ->
                context.startActivity(entry.launchIntent)
                drawerVisible = false
                menuEntry = null
            }

            val onLongPress: (AppEntry) -> Unit = { entry ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                menuEntry = entry
            }

            IOSLauncherTheme(
                darkTheme = when (theme) {
                    AppTheme.AUTO -> isSystemInDarkTheme()
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                },
            ) {
                Box(Modifier.fillMaxSize()) {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { drawerVisible = true },
                        onOpenSettings = { settingsVisible = true },
                        onLongPress = onLongPress,
                    )
                    if (!firstRunDone && !isDefault) {
                        DefaultLauncherCard(
                            onMakeDefault = {
                                context.startActivity(LauncherIntents.requestHomeRole(context))
                            },
                            onLater = { viewModel.setFirstRunDone() },
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
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
                            onOpen = onOpenApp,
                            onLongPress = onLongPress,
                            onClose = { drawerVisible = false },
                        )
                    }
                    ContextMenu(
                        entry = menuEntry,
                        icon = menuEntry?.let { icons[iconCacheKey(it.packageName, it.className)] },
                        onDismiss = { menuEntry = null },
                        onAppInfo = {
                            menuEntry?.let { context.startActivity(LauncherIntents.appInfo(it.packageName)) }
                        },
                        onRemoveFromHome = menuEntry?.let { entry ->
                            if (combinedApps.take(4).any { it.packageName == entry.packageName }) {
                                { viewModel.removeFromPinned(entry.packageName) }
                            } else {
                                null
                            }
                        },
                        onDelete = menuEntry?.let { entry ->
                            if (entry.isSystem) {
                                null
                            } else {
                                { context.startActivity(LauncherIntents.delete(entry.packageName)) }
                            }
                        },
                    )
                    if (settingsVisible) {
                        SettingsSheet(
                            showLabels = showLabels,
                            theme = theme,
                            onShowLabelsChange = { viewModel.setShowLabels(it) },
                            onThemeChange = { viewModel.setTheme(it) },
                            onResetLayout = { viewModel.resetLayout() },
                            onDismiss = { settingsVisible = false },
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        packageReceiver.onChange = null
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }
}
