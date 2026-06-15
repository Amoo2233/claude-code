package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.Wallpaper

/**
 * Sample data for previews. Coil does not fetch images inside `@Preview`, so the cards render their
 * shimmer/error states — the overlay gradient, premium badge, title/attribution, and favorite
 * toggle are all still visible, which is what these previews are meant to exercise.
 */
private val sampleWallpapers = listOf(
    Wallpaper(1, "Aurora Peaks", "Elena Frost", "https://example.com/1.jpg", isPremium = true),
    Wallpaper(2, "Neon City", "Kai Tanaka", "https://example.com/2.jpg"),
    Wallpaper(3, "Golden Dunes", "Omar Said", "https://example.com/3.jpg", isPremium = true),
    Wallpaper(4, "Misty Forest", "Lena Park", "https://example.com/4.jpg"),
    Wallpaper(5, "Coral Reef", "Marco Diaz", "https://example.com/5.jpg"),
    Wallpaper(6, "Starlit Sky", "Aria Vance", "https://example.com/6.jpg", isPremium = true)
)

/** Phone portrait — 2 columns, with one card favorited. */
@Preview(name = "Grid · Phone", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun WallpaperGridPhonePreview() {
    var favorites by remember { mutableStateOf(setOf(1)) }
    ResponsiveWallpaperGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0C10)),
        wallpapers = sampleWallpapers,
        favoriteIds = favorites,
        onFavoriteClick = { w ->
            favorites = if (w.id in favorites) favorites - w.id else favorites + w.id
        },
        onWallpaperClick = {}
    )
}

/** Tablet landscape — adaptive columns kick in at wider widths. */
@Preview(name = "Grid · Tablet", showBackground = true, widthDp = 900, heightDp = 600)
@Composable
private fun WallpaperGridTabletPreview() {
    ResponsiveWallpaperGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0C10)),
        wallpapers = sampleWallpapers,
        favoriteIds = setOf(3),
        onFavoriteClick = {},
        onWallpaperClick = {}
    )
}

/** Loading state — 12 shimmer skeleton cells. */
@Preview(name = "Grid · Loading", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun WallpaperGridLoadingPreview() {
    ResponsiveWallpaperGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0C10)),
        wallpapers = emptyList(),
        favoriteIds = emptySet(),
        onFavoriteClick = {},
        onWallpaperClick = {},
        isLoading = true
    )
}

/** A single card in isolation. */
@Preview(name = "Card", showBackground = true, widthDp = 200, heightDp = 300)
@Composable
private fun WallpaperCardPreview() {
    ResponsiveWallpaperCard(
        wallpaper = sampleWallpapers.first(),
        isFavorited = true,
        onFavoriteClick = {},
        onWallpaperClick = {}
    )
}
