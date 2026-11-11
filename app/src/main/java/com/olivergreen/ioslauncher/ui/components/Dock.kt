package com.olivergreen.ioslauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.ui.theme.GlassWhite
import kotlin.math.roundToInt

@Composable
fun Dock(
    pinned: List<AppEntry>,
    icons: Map<String, ImageBitmap>,
    blurred: ImageBitmap?,
    onOpen: (AppEntry) -> Unit,
    onLongClick: ((AppEntry) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(84.dp)
            .clip(DockShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            if (blurred != null) {
                drawImage(
                    image = blurred,
                    dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                )
            }
            drawRect(color = GlassWhite)
        }
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(4) { index ->
                val entry = pinned.getOrNull(index)
                if (entry != null) {
                    AppIcon(
                        entry = entry,
                        icon = icons[entry.iconKey],
                        showLabel = false,
                        onClick = { onOpen(entry) },
                        onLongClick = onLongClick?.let { { it(entry) } },
                        iconSize = 56.dp,
                        modifier = Modifier.weight(1f).fillMaxSize(),
                    )
                } else {
                    Spacer(Modifier.weight(1f).fillMaxSize())
                }
            }
        }
    }
}
