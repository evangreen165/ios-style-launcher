package com.olivergreen.ioslauncher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.ui.theme.PlaceholderBlue
import com.olivergreen.ioslauncher.ui.theme.White

@Composable
fun ContextMenu(
    entry: AppEntry?,
    icon: ImageBitmap?,
    onDismiss: () -> Unit,
    onAppInfo: () -> Unit,
    onRemoveFromHome: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = entry != null,
        enter = scaleIn(
            initialScale = 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        ) + fadeIn(),
        exit = scaleOut(targetScale = 0.6f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeOut(),
    ) {
        entry?.let { entry ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .widthIn(max = 300.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xF21E1E22))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        .padding(vertical = 10.dp),
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(52.dp).clip(SquircleShape())) {
                                if (icon != null) {
                                    Image(
                                        bitmap = icon,
                                        contentDescription = entry.label,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                } else {
                                    Box(
                                        Modifier.fillMaxSize().background(PlaceholderBlue),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = entry.label.firstOrNull()?.uppercase() ?: "?",
                                            color = White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                            Column(Modifier.padding(start = 12.dp)) {
                                Text(
                                    text = entry.label,
                                    color = White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = entry.packageName,
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                        ContextMenuItem(
                            icon = Icons.Filled.Info,
                            text = "О приложении",
                            onClick = { onAppInfo(); onDismiss() },
                        )
                        if (onRemoveFromHome != null) {
                            ContextMenuItem(
                                icon = Icons.Filled.Home,
                                text = "Убрать с экрана",
                                onClick = { onRemoveFromHome(); onDismiss() },
                            )
                        }
                        if (onDelete != null) {
                            ContextMenuItem(
                                icon = Icons.Filled.Delete,
                                text = "Удалить",
                                tint = Color(0xFFFF6B5E),
                                onClick = { onDelete(); onDismiss() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    tint: Color = Color(0xFF9AA0A6),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(text = text, color = Color(0xFFE8EAED), fontSize = 14.sp)
    }
}
