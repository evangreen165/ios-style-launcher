package com.olivergreen.ioslauncher.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Immutable
data class WallpaperData(
    val id: Int,
    val full: ImageBitmap,
    val blurred: ImageBitmap,
)

class WallpaperProvider(private val context: Context) {

    private val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(context)

    private val _wallpaper = MutableStateFlow<WallpaperData?>(null)
    val wallpaper: StateFlow<WallpaperData?> = _wallpaper.asStateFlow()

    private var currentId: Int = Int.MIN_VALUE

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val id = currentWallpaperId()
        if (id == currentId && _wallpaper.value != null) return@withContext
        val full = loadWallpaperBitmap() ?: gradientFallback()
        val blurred = blur(full)
        currentId = id
        _wallpaper.value = WallpaperData(id, full.asImageBitmap(), blurred.asImageBitmap())
    }

    private fun currentWallpaperId(): Int = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wallpaperManager.getWallpaperId(WallpaperManager.FLAG_SYSTEM)
        } else {
            FALLBACK_ID
        }
    } catch (e: Throwable) {
        FALLBACK_ID
    }

    private fun loadWallpaperBitmap(): Bitmap? {
        val drawable = try {
            wallpaperManager.getFastDrawable() ?: wallpaperManager.drawable
        } catch (e: Throwable) {
            null
        }
        drawableToBitmap(drawable)?.let { return it }
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM)?.use { pfd ->
                    FileInputStream(pfd.fileDescriptor).use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            if (bitmap != null) return bitmap.copy(Bitmap.Config.ARGB_8888, false)
        }
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        if (width <= 0 || height <= 0) return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(Canvas(bitmap))
        return bitmap
    }

    private fun gradientFallback(): Bitmap {
        val metrics = context.resources.displayMetrics
        val width = maxOf(720, metrics.widthPixels)
        val height = maxOf(1280, metrics.heightPixels)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f,
                width.toFloat(), height.toFloat(),
                intArrayOf(
                    0xFF243B55.toInt(),
                    0xFF141E30.toInt(),
                    0xFF0B0D12.toInt(),
                ),
                null,
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    private fun blur(src: Bitmap): Bitmap {
        val downWidth = maxOf(MIN_BLUR_DIMENSION, src.width / BLUR_DIVISOR)
        val downHeight = maxOf(MIN_BLUR_DIMENSION, src.height / BLUR_DIVISOR)
        val downscaled = Bitmap.createScaledBitmap(src, downWidth, downHeight, true)
        val upWidth = maxOf(MIN_BLUR_DIMENSION, src.width / BLUR_FINAL_DIVISOR)
        val upHeight = maxOf(MIN_BLUR_DIMENSION, src.height / BLUR_FINAL_DIVISOR)
        return Bitmap.createScaledBitmap(downscaled, upWidth, upHeight, true)
    }

    private companion object {
        const val BLUR_DIVISOR = 12
        const val BLUR_FINAL_DIVISOR = 4
        const val MIN_BLUR_DIMENSION = 64
        const val FALLBACK_ID = 0
    }
}
