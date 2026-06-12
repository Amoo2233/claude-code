package com.aiwallpapers.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesStore(context: Context) {

    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    private val _ids = MutableStateFlow(
        prefs.getStringSet(KEY, emptySet()).orEmpty().toSet()
    )
    val ids: StateFlow<Set<String>> = _ids.asStateFlow()

    fun toggle(id: String) {
        val updated = _ids.value.let { if (id in it) it - id else it + id }
        _ids.value = updated
        prefs.edit().putStringSet(KEY, updated).apply()
    }

    private companion object {
        const val KEY = "favoriteWallpaperIds"
    }
}
