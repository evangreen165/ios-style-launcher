package com.olivergreen.ioslauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val DarkColors = darkColorScheme(
    primary = White,
    onPrimary = Black,
    background = Color(0xFF0B0D12),
    onBackground = White,
    surface = Color(0xFF1C1C20),
    onSurface = White,
)

private val LightColors = lightColorScheme(
    primary = Black,
    onPrimary = White,
    background = Color(0xFFF2F2F7),
    onBackground = Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1E),
)

@Composable
fun IOSLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    glassEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    val launcherColors = remember(scheme) {
        val dark = scheme.surface.luminance() < 0.5f
        LauncherColors(
            panel = scheme.surface,
            glassTint = if (dark) Color(0x40FFFFFF) else Color(0x66FFFFFF),
            cardBackground = if (dark) Color(0x59000000) else Color(0xCCFFFFFF),
            pillBackground = if (dark) Color(0x59000000) else Color(0xCCFFFFFF),
            searchField = scheme.surface,
            fieldText = scheme.onSurface,
            textPrimary = scheme.onSurface,
            textSecondary = scheme.onSurface.copy(alpha = 0.7f),
            labelText = scheme.onSurface,
            highlight = if (dark) Color(0x59FFFFFF) else Color(0x33777777),
            wallpaperFallbackStart = if (dark) Color(0xFF243B55) else Color(0xFFB8C6DB),
            wallpaperFallbackEnd = if (dark) Color(0xFF0B0D12) else Color(0xFF5A6E8A),
        )
    }
    CompositionLocalProvider(
        LocalLauncherColors provides launcherColors,
        LocalGlassEnabled provides glassEnabled,
        LocalLauncherIsDark provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            content = content,
        )
    }
}
