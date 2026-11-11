package com.olivergreen.ioslauncher.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
        if (id == currentId) return@withContext
        val drawable = systemWallpaperDrawable() ?: return@withContext
        val full = drawableToBitmap(drawable) ?: return@withContext
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

    private fun systemWallpaperDrawable(): Drawable? = try {
        wallpaperManager.drawable
    } catch (e: Throwable) {
        null
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            if (bitmap != null) return bitmap.copy(Bitmap.Config.ARGB_8888, false)
        }
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        if (width <= 0 || height <= 0) return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(android.graphics.Canvas(bitmap))
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
