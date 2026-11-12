package com.olivergreen.ioslauncher.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.ui.theme.LabelShadow
import com.olivergreen.ioslauncher.ui.theme.PlaceholderBlue
import com.olivergreen.ioslauncher.ui.theme.White
import kotlin.math.abs
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    entry: AppEntry,
    icon: ImageBitmap?,
    showLabel: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onDragStart: (() -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: ((Offset) -> Unit)? = null,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "iconScale",
    )
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Column(
        modifier = modifier
            .onGloballyPositioned { coordinates = it }
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
                onLongClick = null,
            )
            .iconDragGestures(
                onLongPress = onLongClick,
                onDragStart = onDragStart,
                onDrag = onDrag?.let { cb ->
                    { local -> coordinates?.let { cb(it.localToRoot(local)) } }
                },
                onDragEnd = onDragEnd?.let { cb ->
                    { local -> coordinates?.let { cb(it.localToRoot(local)) } }
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(SquircleShape())
                .then(
                    if (highlighted) {
                        Modifier.background(Color(0x59FFFFFF))
                    } else {
                        Modifier
                    }
                ),
        ) {
            if (icon != null) {
                Image(
                    bitmap = icon,
                    contentDescription = entry.label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                PlaceholderIcon(entry.label)
            }
        }
        if (showLabel) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    shadow = Shadow(
                        color = LabelShadow,
                        offset = Offset(0f, 1f),
                        blurRadius = 2f,
                    ),
                ),
                modifier = Modifier.widthIn(max = iconSize + 16.dp),
            )
        }
    }
}

private enum class EarlyResult { MOVED, RELEASED }

private fun Modifier.iconDragGestures(
    onLongPress: (() -> Unit)?,
    onDragStart: (() -> Unit)?,
    onDrag: ((Offset) -> Unit)?,
    onDragEnd: ((Offset) -> Unit)?,
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val touchSlop = viewConfiguration.touchSlop
        val longPressTimeout = viewConfiguration.longPressTimeoutMillis

        val early = withTimeoutOrNull(longPressTimeout) {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == down.id }
                    ?: return@withTimeoutOrNull EarlyResult.RELEASED
                if (change.isConsumed) return@withTimeoutOrNull EarlyResult.MOVED
                if ((change.position - down.position).getDistance() > touchSlop) {
                    return@withTimeoutOrNull EarlyResult.MOVED
                }
                if (event.type == PointerEventType.Release) {
                    return@withTimeoutOrNull EarlyResult.RELEASED
                }
            }
            EarlyResult.RELEASED
        }

        when (early) {
            EarlyResult.MOVED -> Unit
            EarlyResult.RELEASED -> Unit
            null -> {
                var dragging = false
                var last = down.position
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    change.consume()
                    last = change.position
                    if (!dragging && (change.position - down.position).getDistance() > touchSlop) {
                        dragging = true
                        onDragStart?.invoke()
                    }
                    if (dragging) onDrag?.invoke(last)
                    if (event.type == PointerEventType.Release) {
                        if (dragging) {
                            onDragEnd?.invoke(last)
                        } else {
                            onLongPress?.invoke()
                        }
                        break
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderIcon(label: String) {
    Box(
        modifier = Modifier.fillMaxSize().clip(SquircleShape()).background(PlaceholderBlue),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.firstOrNull()?.uppercase() ?: "?",
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

val DockShape = RoundedCornerShape(28.dp)
