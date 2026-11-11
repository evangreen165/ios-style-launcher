package com.olivergreen.ioslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.HOME_CELLS_PER_PAGE
import com.olivergreen.ioslauncher.data.HOME_COLUMNS
import com.olivergreen.ioslauncher.data.HOME_ROWS
import com.olivergreen.ioslauncher.data.pageCountFor
import com.olivergreen.ioslauncher.ui.components.AppIcon
import com.olivergreen.ioslauncher.ui.components.Dock
import com.olivergreen.ioslauncher.ui.components.PageDots
import com.olivergreen.ioslauncher.ui.components.WallpaperBackdrop
import com.olivergreen.ioslauncher.ui.theme.White
import com.olivergreen.ioslauncher.viewmodel.HomeViewModel

private const val COLUMNS = HOME_COLUMNS
private const val ROWS = HOME_ROWS
private const val CELLS_PER_PAGE = HOME_CELLS_PER_PAGE

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
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
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 4.dp, end = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Настройки",
                tint = Color.White.copy(alpha = 0.55f),
                modifier = Modifier.size(18.dp),
            )
        }
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
            Spacer(Modifier.height(8.dp))
            SearchPill(
                onClick = onOpenDrawer,
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
                            icon = icons[entry.iconKey],
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

@Composable
private fun SearchPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(168.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Color(0x59000000))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = White.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Поиск",
                color = White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
