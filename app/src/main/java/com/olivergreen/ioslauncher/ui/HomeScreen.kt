package com.olivergreen.ioslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

private const val COLUMNS = HOME_COLUMNS
private const val ROWS = HOME_ROWS
private const val CELLS_PER_PAGE = HOME_CELLS_PER_PAGE

private val ClockFormat = DateTimeFormatter.ofPattern("HH:mm")
private val DateFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("ru"))
private val MonthFormat = DateTimeFormatter.ofPattern("LLLL", Locale("ru"))

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
    val haptic = LocalHapticFeedback.current

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = {
            1 + if (apps.isEmpty()) 0 else pageCountFor(apps.size)
        },
    )

    LaunchedEffect(Unit) {
        if (pagerState.currentPage != 1) {
            pagerState.scrollToPage(1)
        }
    }

    val gridRects = remember { mutableStateMapOf<Int, Rect>() }
    val dockRects = remember { mutableStateMapOf<Int, Rect>() }
    var dragging by remember { mutableStateOf<AppEntry?>(null) }
    var dragCenter by remember { mutableStateOf(Offset.Zero) }
    var dropTarget by remember { mutableStateOf<DropTarget?>(null) }

    val onDragMove: (Offset) -> Unit = { root ->
        dragCenter = root
        dropTarget = dockRects.entries.firstOrNull { it.value.contains(root) }
            ?.let { DropTarget(index = it.key, inDock = true) }
            ?: gridRects.entries.firstOrNull { it.value.contains(root) }
                ?.let { DropTarget(index = it.key, inDock = false) }
    }

    val onDragDrop: (Offset) -> Unit = {
        val entry = dragging
        val target = dropTarget
        if (entry != null && target != null) {
            viewModel.moveToIndex(entry.packageName, target.index)
        }
        dragging = null
        dropTarget = null
        dragCenter = Offset.Zero
    }

    val startDrag: (AppEntry) -> Unit = { entry ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        dragging = entry
    }

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
                .navigationBarsPadding(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) { page ->
                if (page == 0) {
                    TodayPage()
                } else {
                    AppPage(
                        page = page - 1,
                        apps = apps,
                        icons = icons,
                        showLabels = showLabels,
                        onOpen = { entry -> context.startActivity(entry.launchIntent) },
                        onLongPress = onLongPress,
                        onDragStart = { entry -> startDrag(entry) },
                        onDrag = onDragMove,
                        onDragEnd = onDragDrop,
                        highlightedIndex = dropTarget?.takeIf { !it.inDock }?.index,
                        registerCell = { index, rect -> gridRects[index] = rect },
                    )
                }
            }
            PageDots(
                count = (pagerState.pageCount - 1).coerceAtLeast(0),
                current = (pagerState.currentPage - 1).coerceAtLeast(0),
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
                onDragStart = { entry -> startDrag(entry) },
                onDrag = onDragMove,
                onDragEnd = onDragDrop,
                dropSlot = dropTarget?.takeIf { it.inDock }?.index,
                registerSlot = { index, rect -> dockRects[index] = rect },
            )
        }
        dragging?.let { entry ->
            val icon = icons[entry.iconKey]
            val overlaySizePx = with(androidx.compose.ui.platform.LocalDensity.current) { OVERLAY_ICON_SIZE.toPx() }
            Box(
                modifier = Modifier
                    .zIndex(10f)
                    .offset {
                        IntOffset(
                            (dragCenter.x - overlaySizePx / 2f).roundToInt(),
                            (dragCenter.y - overlaySizePx / 2f).roundToInt(),
                        )
                    }
                    .size(OVERLAY_ICON_SIZE)
                    .graphicsLayer {
                        scaleX = 1.15f
                        scaleY = 1.15f
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    androidx.compose.foundation.Image(
                        bitmap = icon,
                        contentDescription = entry.label,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).background(Color(0xFF1D6FA5)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = entry.label.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

private class DropTarget(
    val index: Int,
    val inDock: Boolean,
)

private val OVERLAY_ICON_SIZE = 64.dp

@Composable
private fun TodayPage() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = LocalDateTime.now()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0x59000000))
                .pressScale()
                .padding(horizontal = 22.dp, vertical = 26.dp),
        ) {
            Text(
                text = now.format(ClockFormat),
                color = Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.Light,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = now.format(DateFormat),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TodayMiniCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = now.dayOfMonth.toString(),
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = now.format(MonthFormat).capitalizeFirst(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                )
            }
            TodayMiniCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Сегодня",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.width(64.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.25f)))
                    Box(Modifier.width(48.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.18f)))
                }
            }
        }
    }
}

@Composable
private fun TodayMiniCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .pressScale()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x4D000000))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        content()
    }
}

@Composable
private fun Modifier.pressScale(): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressScale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
        .clickable(
            interactionSource = interaction,
            indication = null,
            onClick = {},
        )
}

private fun String.capitalizeFirst(): String =
    if (isEmpty()) this else replaceFirstChar { it.uppercase(Locale("ru")) }

@Composable
private fun AppPage(
    page: Int,
    apps: List<AppEntry>,
    icons: Map<String, ImageBitmap>,
    showLabels: Boolean,
    onOpen: (AppEntry) -> Unit,
    onLongPress: ((AppEntry) -> Unit)?,
    onDragStart: ((AppEntry) -> Unit)?,
    onDrag: ((Offset) -> Unit)?,
    onDragEnd: ((Offset) -> Unit)?,
    highlightedIndex: Int?,
    registerCell: ((Int, Rect) -> Unit)?,
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
                            onDragStart = onDragStart?.let { { it(entry) } },
                            onDrag = onDrag,
                            onDragEnd = onDragEnd,
                            highlighted = highlightedIndex == index,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .onGloballyPositioned { coords ->
                                    registerCell?.invoke(index, coords.boundsInRoot())
                                },
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
