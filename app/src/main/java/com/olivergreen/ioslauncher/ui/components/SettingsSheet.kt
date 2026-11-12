package com.olivergreen.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.data.AppTheme
import com.olivergreen.ioslauncher.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    showLabels: Boolean,
    theme: AppTheme,
    glassEffect: Boolean,
    onShowLabelsChange: (Boolean) -> Unit,
    onThemeChange: (AppTheme) -> Unit,
    onGlassEffectChange: (Boolean) -> Unit,
    onResetLayout: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E22),
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            Text(
                text = "Настройки",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = "Подписи", color = White, fontSize = 15.sp)
                    Text(
                        text = "Показывать названия под иконками",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                    )
                }
                Switch(
                    checked = showLabels,
                    onCheckedChange = onShowLabelsChange,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Color(0xFF1D6FA5),
                        checkedThumbColor = White,
                    ),
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = "Эффект матового стекла", color = White, fontSize = 15.sp)
                    Text(
                        text = "Полупрозрачные панели (выкл. — сплошной цвет, экономия батареи)",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                    )
                }
                Switch(
                    checked = glassEffect,
                    onCheckedChange = onGlassEffectChange,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Color(0xFF1D6FA5),
                        checkedThumbColor = White,
                    ),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(text = "Тема", color = White, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeOption("Авто", theme == AppTheme.AUTO) { onThemeChange(AppTheme.AUTO) }
                ThemeOption("Светлая", theme == AppTheme.LIGHT) { onThemeChange(AppTheme.LIGHT) }
                ThemeOption("Тёмная", theme == AppTheme.DARK) { onThemeChange(AppTheme.DARK) }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable(onClick = onResetLayout)
                    .padding(14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Сбросить раскладку",
                    color = Color(0xFFFF8A80),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color(0xFF1D6FA5) else Color(0x33FFFFFF))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = if (selected) White else White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
