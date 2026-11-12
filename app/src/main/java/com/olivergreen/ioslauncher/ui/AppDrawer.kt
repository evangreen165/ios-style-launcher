package com.olivergreen.ioslauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.ui.components.AppIcon
import com.olivergreen.ioslauncher.ui.theme.GlassWhite
import kotlin.math.roundToInt

@Composable
fun AppDrawer(
    apps: List<AppEntry>,
    icons: Map<String, ImageBitmap>,
    showLabels: Boolean,
    blurred: ImageBitmap?,
    onOpen: (AppEntry) -> Unit,
    onLongPress: ((AppEntry) -> Unit)?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val gridState = rememberLazyGridState()
    val filtered by remember(apps) {
        derivedStateOf { filterApps(apps, query) }
    }

    BackHandler(onBack = onClose)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onVerticalSwipe(
                onSwipeUp = {},
                onSwipeDown = {
                    if (gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset <= 2) {
                        onClose()
                    }
                },
            ),
    ) {
        Box(Modifier.fillMaxSize().graphicsLayer { }) {
            Canvas(Modifier.fillMaxSize()) {
                if (blurred != null) {
                    drawImage(
                        image = blurred,
                        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                    )
                }
                drawRect(color = GlassWhite)
            }
        }
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                onClose = onClose,
            )
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Ничего не найдено",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 15.sp,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 10.dp,
                        bottom = 16.dp,
                    ),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
                ) {
                    items(filtered, key = { it.iconKey }) { entry ->
                        AppIcon(
                            entry = entry,
                            icon = icons[entry.iconKey],
                            showLabel = showLabels,
                            onClick = { onOpen(entry) },
                            onLongClick = onLongPress?.let { { it(entry) } },
                            iconSize = 56.dp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
