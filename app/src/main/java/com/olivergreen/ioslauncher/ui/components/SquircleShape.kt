package com.olivergreen.ioslauncher.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin

class SquircleShape(private val exponent: Float = 4f) : Shape {

    private val pathCache = HashMap<Long, Path>()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val key = (size.width.toInt().toLong() shl 32) or (size.height.toInt().toLong() and 0xFFFFFFFFL)
        val path = pathCache.getOrPut(key) { buildPath(size) }
        return Outline.Generic(path)
    }

    private fun buildPath(size: Size): Path {
        val halfW = size.width / 2f
        val halfH = size.height / 2f
        val exp = 2.0 / exponent
        val path = Path()
        path.moveTo(size.width, halfH)
        val steps = 96
        for (i in 1..steps) {
            val t = i * 2.0 * PI / steps
            val cosT = cos(t)
            val sinT = sin(t)
            val x = halfW + halfW * sign(cosT) * abs(cosT).pow(exp)
            val y = halfH + halfH * sign(sinT) * abs(sinT).pow(exp)
            path.lineTo(x.toFloat(), y.toFloat())
        }
        path.close()
        return path
    }
}
