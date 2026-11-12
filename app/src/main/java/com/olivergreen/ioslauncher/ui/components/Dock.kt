package com.olivergreen.ioslauncher.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.ui.theme.glassEnabled
import com.olivergreen.ioslauncher.ui.theme.launcherColors
import kotlin.math.roundToInt

@Composable
fun Dock(
    pinned: List<AppEntry>,
    icons: Map<String, ImageBitmap>,
    blurred: ImageBitmap?,
    onOpen: (AppEntry) -> Unit,
    onLongClick: ((AppEntry) -> Unit)? = null,
    onDragStart: ((AppEntry) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: ((Offset) -> Unit)? = null,
    dropSlot: Int? = null,
    registerSlot: ((Int, Rect) -> Unit)? = null,
    settleTick: Int = 0,
    modifier: Modifier = Modifier,
) {
    val colors = launcherColors()
    val glassOn = glassEnabled()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(84.dp)
            .clip(DockShape),
    ) {
        Box(Modifier.fillMaxSize().graphicsLayer { }) {
            Canvas(Modifier.fillMaxSize()) {
                if (glassOn) {
                    drawRect(color = colors.panel)
                    if (blurred != null) {
                        drawImage(
                            image = blurred,
                            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                        )
                    }
                    drawRect(color = colors.glassTint)
                } else {
                    drawRect(color = colors.panel)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(4) { index ->
                val entry = pinned.getOrNull(index)
                if (entry != null) {
                    val settleScale = remember(entry.packageName, settleTick) {
                        Animatable(1f)
                    }
                    LaunchedEffect(settleTick, entry.packageName) {
                        if (settleTick > 0) {
                            settleScale.snapTo(0.82f)
                            settleScale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                ),
                            )
                        }
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = settleScale.value
                                scaleY = settleScale.value
                            }
                            .onGloballyPositioned { coords ->
                                registerSlot?.invoke(index, coords.boundsInRoot())
                            },
                    ) {
                        AppIcon(
                            entry = entry,
                            icon = icons[entry.iconKey],
                            showLabel = false,
                            onClick = { onOpen(entry) },
                            onLongClick = onLongClick?.let { { it(entry) } },
                            onDragStart = onDragStart?.let { { it(entry) } },
                            onDrag = onDrag,
                            onDragEnd = onDragEnd,
                            highlighted = dropSlot == index,
                            iconSize = 56.dp,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    Spacer(Modifier.weight(1f).fillMaxSize())
                }
            }
        }
    }
}
