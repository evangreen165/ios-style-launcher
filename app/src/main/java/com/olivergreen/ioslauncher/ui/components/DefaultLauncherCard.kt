package com.olivergreen.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.ui.theme.White

@Composable
fun DefaultLauncherCard(
    onMakeDefault: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xE6202024)),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = "Сделать iOS Launcher основным",
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Нажмите кнопку, чтобы назначить этот лаунчер домашним экраном",
                color = White.copy(alpha = 0.7f),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                TextButton(onClick = onLater) {
                    Text("Позже", color = White.copy(alpha = 0.8f))
                }
                Button(
                    onClick = onMakeDefault,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D6FA5),
                        contentColor = White,
                    ),
                ) {
                    Text("Сделать основным", fontSize = 13.sp)
                }
            }
        }
    }
}
