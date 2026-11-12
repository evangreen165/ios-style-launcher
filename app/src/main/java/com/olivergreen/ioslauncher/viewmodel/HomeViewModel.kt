package com.olivergreen.ioslauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olivergreen.ioslauncher.data.AppEntry
import com.olivergreen.ioslauncher.data.AppRepository
import com.olivergreen.ioslauncher.data.AppTheme
import com.olivergreen.ioslauncher.data.IconLoader
import com.olivergreen.ioslauncher.data.SettingsStore
import com.olivergreen.ioslauncher.data.mergePinnedFirst
import com.olivergreen.ioslauncher.util.WallpaperProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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
    val homeOrder: StateFlow<List<String>> =
        settings.homeOrder.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val showLabels: StateFlow<Boolean> =
        settings.showLabels.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val theme: StateFlow<AppTheme> =
        settings.theme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppTheme.AUTO)
    val firstRunDone: StateFlow<Boolean> =
        settings.firstRunDone.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val glassEffect: StateFlow<Boolean> =
        settings.glassEffect.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val combinedApps: StateFlow<List<AppEntry>> =
        combine(apps, homeOrder) { list, order ->
            mergePinnedFirst(list, order)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    fun removeFromHome(packageName: String) {
        viewModelScope.launch {
            val current = settings.homeOrder.first()
            settings.setHomeOrder(current.filterNot { it == packageName })
        }
    }

    fun moveToIndex(packageName: String, targetIndex: Int) {
        viewModelScope.launch {
            val current = combinedApps.value
            if (current.isEmpty()) return@launch
            val from = current.indexOfFirst { it.packageName == packageName }
            if (from < 0) return@launch
            val target = targetIndex.coerceIn(0, current.size - 1)
            if (from == target) return@launch
            val list = current.toMutableList()
            val entry = list.removeAt(from)
            list.add(target, entry)
            settings.setHomeOrder(list.map { it.packageName })
        }
    }

    fun resetLayout() {
        viewModelScope.launch { settings.setHomeOrder(emptyList()) }
    }

    fun setShowLabels(show: Boolean) {
        viewModelScope.launch { settings.setShowLabels(show) }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settings.setTheme(theme) }
    }

    fun setFirstRunDone() {
        viewModelScope.launch { settings.setFirstRunDone() }
    }

    fun setGlassEffect(enabled: Boolean) {
        viewModelScope.launch { settings.setGlassEffect(enabled) }
    }
}
