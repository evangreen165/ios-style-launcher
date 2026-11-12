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

val DarkLauncherColors = LauncherColors(
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

val LightLauncherColors = LauncherColors(
    panel = Color(0xE6FFFFFF),
    glassTint = Color(0x66FFFFFF),
    cardBackground = Color(0xB3FFFFFF),
    pillBackground = Color(0x59FFFFFF),
    searchField = Color(0xE6FFFFFF),
    fieldText = Color(0xFF1C1C1E),
    textPrimary = Color(0xFF1C1C1E),
    textSecondary = Color(0xCC3C3C43),
    labelText = Color(0xFF1C1C1E),
    highlight = Color(0x59FFFFFF),
    wallpaperFallbackStart = Color(0xFFB8C6DB),
    wallpaperFallbackEnd = Color(0xFF5A6E8A),
)

val LocalLauncherColors = staticCompositionLocalOf { DarkLauncherColors }

val LocalGlassEnabled = staticCompositionLocalOf { true }

@Composable
fun launcherColors(): LauncherColors = LocalLauncherColors.current

@Composable
fun glassEnabled(): Boolean = LocalGlassEnabled.current
