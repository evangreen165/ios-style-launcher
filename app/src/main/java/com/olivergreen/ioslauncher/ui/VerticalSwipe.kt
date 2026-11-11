package com.olivergreen.ioslauncher.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

fun Modifier.onVerticalSwipe(
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
): Modifier = pointerInput(Unit) {
    val threshold = 80.dp.toPx()
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var totalX = 0f
        var totalY = 0f
        var consumed = false
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (change.isConsumed) {
                consumed = true
                break
            }
            val delta = change.position - change.previousPosition
            totalX += delta.x
            totalY += delta.y
            if (event.type == PointerEventType.Release) {
                break
            }
        }
        if (!consumed && totalY != 0f && abs(totalY) > threshold && abs(totalX) < abs(totalY) * 0.6f) {
            if (totalY < 0f) onSwipeUp() else onSwipeDown()
        }
    }
}
