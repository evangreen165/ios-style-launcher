package com.olivergreen.ioslauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.AppRepository
import com.olivergreen.ioslauncher.data.IconLoader
import com.olivergreen.ioslauncher.data.SettingsStore
import com.olivergreen.ioslauncher.util.WallpaperProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val iconLoader = IconLoader(application, viewModelScope)
    private val settings = SettingsStore(application)
    private val wallpaper = WallpaperProvider(application)

    private val _apps = MutableStateFlow<List<AppEntry>>(emptyList())
    val apps: StateFlow<List<AppEntry>> = _apps.asStateFlow()

    val icons = iconLoader.icons
    val wallpaperData = wallpaper.wallpaper
    val pinnedOrder = settings.pinnedOrder
    val showLabels = settings.showLabels
    val theme = settings.theme
    val firstRunDone = settings.firstRunDone

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            val loaded = repository.loadApps()
            _apps.value = loaded
            iconLoader.loadAll(loaded)
        }
    }

    fun refreshWallpaper() {
        viewModelScope.launch { wallpaper.refresh() }
    }
}
