package com.example.data

/**
 * A single wallpaper entry rendered by the responsive grid.
 *
 * @property id Stable unique identifier (used as the lazy-grid item key and in test tags).
 * @property title Display title shown on the card.
 * @property photographer Attribution shown beneath the title.
 * @property standardUrl Remote URL of the full-size image loaded by Coil.
 * @property thumbnailUrl Optional smaller URL suitable for grid thumbnails.
 * @property isPremium Whether the "4K VIP" badge is shown.
 */
data class Wallpaper(
    val id: Int,
    val title: String,
    val photographer: String,
    val standardUrl: String,
    val thumbnailUrl: String = standardUrl,
    val isPremium: Boolean = false
)
