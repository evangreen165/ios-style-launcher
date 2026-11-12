package com.olivergreen.ioslauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

private val DarkColors = darkColorScheme(
    primary = White,
    onPrimary = Black,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
)

private val LightColors = lightColorScheme(
    primary = Black,
    onPrimary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
)

@Composable
fun IOSLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    glassEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val launcherColors = remember(darkTheme) {
        if (darkTheme) DarkLauncherColors else LightLauncherColors
    }
    CompositionLocalProvider(
        LocalLauncherColors provides launcherColors,
        LocalGlassEnabled provides glassEnabled,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content,
        )
    }
}
