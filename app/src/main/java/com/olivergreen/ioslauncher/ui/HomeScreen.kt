package com.olivergreen.ioslauncher.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.iconCacheKey
import com.olivergreen.ioslauncher.ui.components.AppIcon
import com.olivergreen.ioslauncher.ui.components.Dock
import com.olivergreen.ioslauncher.ui.components.PageDots
import com.olivergreen.ioslauncher.ui.components.WallpaperBackdrop
import com.olivergreen.ioslauncher.viewmodel.HomeViewModel
import kotlin.math.abs

private const val COLUMNS = 4
private const val ROWS = 6
private const val CELLS_PER_PAGE = COLUMNS * ROWS

fun pageCountFor(appCount: Int): Int =
    if (appCount <= 0) 1 else (appCount + CELLS_PER_PAGE - 1) / CELLS_PER_PAGE

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenDrawer: () -> Unit,
    onLongPress: ((AppEntry) -> Unit)? = null,
) {
    val apps by viewModel.combinedApps.collectAsState()
    val icons by viewModel.icons.collectAsState()
    val wallpaper by viewModel.wallpaperData.collectAsState()
    val showLabels by viewModel.showLabels.collectAsState()
    val context = LocalContext.current

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCountFor(apps.size) })

    Box(
        Modifier
            .fillMaxSize()
            .onVerticalSwipe(
                onSwipeUp = onOpenDrawer,
                onSwipeDown = {},
            ),
    ) {
        WallpaperBackdrop(full = wallpaper?.full)
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 10.dp),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) { page ->
                AppPage(
                    page = page,
                    apps = apps,
                    icons = icons,
                    showLabels = showLabels,
                    onOpen = { entry -> context.startActivity(entry.launchIntent) },
                    onLongPress = onLongPress,
                )
            }
            PageDots(
                count = pagerState.pageCount,
                current = pagerState.currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(10.dp))
            Dock(
                pinned = apps.take(4),
                icons = icons,
                blurred = wallpaper?.blurred,
                onOpen = { entry -> context.startActivity(entry.launchIntent) },
                onLongClick = onLongPress,
            )
        }
    }
}

@Composable
private fun AppPage(
    page: Int,
    apps: List<AppEntry>,
    icons: Map<String, ImageBitmap>,
    showLabels: Boolean,
    onOpen: (AppEntry) -> Unit,
    onLongPress: ((AppEntry) -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (row in 0 until ROWS) {
            Row(Modifier.fillMaxWidth().weight(1f)) {
                for (col in 0 until COLUMNS) {
                    val index = page * CELLS_PER_PAGE + row * COLUMNS + col
                    if (index < apps.size) {
                        val entry = apps[index]
                        AppIcon(
                            entry = entry,
                            icon = icons[iconCacheKey(entry.packageName, entry.className)],
                            showLabel = showLabels,
                            onClick = { onOpen(entry) },
                            onLongClick = onLongPress?.let { { it(entry) } },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    } else {
                        Spacer(Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

private fun Modifier.onVerticalSwipe(
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
): Modifier = pointerInput(Unit) {
    val threshold = 80.dp.toPx()
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            var totalX = 0f
            var totalY = 0f
            var horizontalConsumed = false
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                if (change.isConsumed) {
                    horizontalConsumed = true
                    break
                }
                val delta = change.position - change.previousPosition
                totalX += delta.x
                totalY += delta.y
                if (event.type == PointerEventType.Release) {
                    break
                }
            }
            if (!horizontalConsumed && totalY != 0f && abs(totalY) > threshold && abs(totalX) < abs(totalY) * 0.6f) {
                if (totalY < 0f) onSwipeUp() else onSwipeDown()
            }
        }
}
