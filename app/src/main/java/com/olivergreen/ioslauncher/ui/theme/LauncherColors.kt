package com.olivergreen.ioslauncher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LauncherColors(
    val panel: Color,
    val glassTint: Color,
    val cardBackground: Color,
    val pillBackground: Color,
    val searchField: Color,
    val fieldText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val labelText: Color,
    val highlight: Color,
    val wallpaperFallbackStart: Color,
    val wallpaperFallbackEnd: Color,
)

val LocalLauncherColors = staticCompositionLocalOf { defaultLauncherColors }

val LocalGlassEnabled = staticCompositionLocalOf { true }

val LocalLauncherIsDark = staticCompositionLocalOf { true }

val defaultLauncherColors: LauncherColors
    get() = LauncherColors(
        panel = Color(0xE61C1C20),
        glassTint = Color(0x33FFFFFF),
        cardBackground = Color(0x59000000),
        pillBackground = Color(0x59000000),
        searchField = Color(0xE6202024),
        fieldText = Color(0xFFFFFFFF),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xCCFFFFFF),
        labelText = Color(0xFFFFFFFF),
        highlight = Color(0x59FFFFFF),
        wallpaperFallbackStart = Color(0xFF243B55),
        wallpaperFallbackEnd = Color(0xFF0B0D12),
    )

@Composable
fun launcherColors(): LauncherColors = LocalLauncherColors.current

@Composable
fun glassEnabled(): Boolean = LocalGlassEnabled.current

@Composable
fun launcherIsDark(): Boolean = LocalLauncherIsDark.current
