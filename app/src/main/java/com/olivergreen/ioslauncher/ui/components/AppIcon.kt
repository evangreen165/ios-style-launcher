package com.olivergreen.ioslauncher.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    entry: AppEntry,
    icon: ImageBitmap?,
    showLabel: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
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

    Column(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Box(modifier = Modifier.size(iconSize).clip(SquircleShape())) {
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
                        offset = androidx.compose.ui.geometry.Offset(0f, 1f),
                        blurRadius = 2f,
                    ),
                ),
                modifier = Modifier.widthIn(max = iconSize + 16.dp),
            )
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
