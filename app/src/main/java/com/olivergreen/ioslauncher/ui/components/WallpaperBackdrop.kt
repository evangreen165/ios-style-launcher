package com.olivergreen.ioslauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.olivergreen.ioslauncher.ui.theme.launcherColors

@Composable
fun WallpaperBackdrop(full: ImageBitmap?, modifier: Modifier = Modifier) {
    val colors = launcherColors()
    Box(modifier.fillMaxSize().graphicsLayer { }) {
        if (full != null) {
            Image(
                bitmap = full,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(colors.wallpaperFallbackStart, colors.wallpaperFallbackEnd),
                        ),
                    ),
            )
        }
    }
}
