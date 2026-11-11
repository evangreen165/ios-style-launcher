package com.olivergreen.ioslauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class IconLoader(context: Context, private val scope: CoroutineScope) {

    private val packageManager: PackageManager = context.packageManager
    private val cache = object : LruCache<String, ImageBitmap>(CACHE_SIZE_BYTES) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = value.width * value.height * 4
    }
    private val loadingMutex = Mutex()

    private val _icons = MutableStateFlow<Map<String, ImageBitmap>>(emptyMap())
    val icons: StateFlow<Map<String, ImageBitmap>> = _icons.asStateFlow()

    fun get(packageName: String, className: String): ImageBitmap? =
        cache.get(key(packageName, className))

    fun loadAll(entries: List<AppEntry>) {
        entries.forEach { load(it.packageName, it.className) }
    }

    fun load(packageName: String, className: String) {
        scope.launch(Dispatchers.IO) {
            val cacheKey = key(packageName, className)
            if (cache.get(cacheKey) != null) return@launch
            loadingMutex.withLock {
                if (cache.get(cacheKey) != null) return@withLock
                val icon = loadIcon(packageName, className) ?: return@withLock
                cache.put(cacheKey, icon)
                _icons.update { it + (cacheKey to icon) }
            }
        }
    }

    private fun loadIcon(packageName: String, className: String): ImageBitmap? {
        val drawable = try {
            packageManager.getActivityIcon(ComponentName(packageName, className))
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return drawableToBitmap(drawable)?.asImageBitmap()
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {        if (drawable == null) return null
        val bitmap = Bitmap.createBitmap(ICON_SIZE_PX, ICON_SIZE_PX, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, ICON_SIZE_PX, ICON_SIZE_PX)
        drawable.draw(canvas)
        return bitmap
    }

    private fun key(packageName: String, className: String): String = "$packageName/$className"

    private companion object {
        const val ICON_SIZE_PX = 144
        const val CACHE_SIZE_BYTES = 8 * 1024 * 1024
    }
}
