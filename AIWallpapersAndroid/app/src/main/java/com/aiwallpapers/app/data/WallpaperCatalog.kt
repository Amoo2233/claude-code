package com.aiwallpapers.app.data

import android.content.Context
import com.aiwallpapers.app.model.Wallpaper
import com.aiwallpapers.app.model.WallpaperManifest
import kotlinx.serialization.json.Json

class WallpaperCatalog(context: Context) {

    val wallpapers: List<Wallpaper> = runCatching {
        val json = context.assets.open("wallpapers.json")
            .bufferedReader().use { it.readText() }
        Json { ignoreUnknownKeys = true }
            .decodeFromString<WallpaperManifest>(json).wallpapers
    }.getOrDefault(emptyList())

    val categories: List<String> = wallpapers.map { it.category }.distinct()

    fun byId(id: String): Wallpaper? = wallpapers.find { it.id == id }

    fun inCategory(category: String): List<Wallpaper> =
        wallpapers.filter { it.category == category }

    fun search(query: String): List<Wallpaper> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return wallpapers
        return wallpapers.filter {
            it.title.contains(trimmed, ignoreCase = true) ||
                it.category.contains(trimmed, ignoreCase = true)
        }
    }
}
