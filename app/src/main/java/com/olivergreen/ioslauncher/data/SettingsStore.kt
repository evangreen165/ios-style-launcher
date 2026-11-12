package com.olivergreen.ioslauncher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "launcher_settings")

enum class AppTheme {
    AUTO, LIGHT, DARK,
}

class SettingsStore(private val context: Context) {

    private object Keys {
        val HOME_ORDER = stringPreferencesKey("home_order")
        val SHOW_LABELS = booleanPreferencesKey("show_labels")
        val THEME = stringPreferencesKey("theme")
        val FIRST_RUN = booleanPreferencesKey("first_run_done")
    }

    val homeOrder: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.HOME_ORDER]?.takeIf { it.isNotBlank() }?.split(",") ?: emptyList()
    }

    val showLabels: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_LABELS] ?: true
    }

    val theme: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.AUTO
    }

    val firstRunDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.FIRST_RUN] ?: false
    }

    suspend fun setHomeOrder(order: List<String>) {
        context.dataStore.edit { it[Keys.HOME_ORDER] = order.joinToString(",") }
    }

    suspend fun setShowLabels(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_LABELS] = show }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun setFirstRunDone() {
        context.dataStore.edit { it[Keys.FIRST_RUN] = true }
    }
}
