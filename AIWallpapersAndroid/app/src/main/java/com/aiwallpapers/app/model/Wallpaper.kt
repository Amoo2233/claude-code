package com.aiwallpapers.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: String,
    val title: String,
    val category: String,
    val prompt: String,
) {
    val thumbnailUrl: String get() = CatalogConfig.imageUrl(id, WallpaperQuality.SD_480)
    fun imageUrl(quality: WallpaperQuality): String = CatalogConfig.imageUrl(id, quality)
}

@Serializable
data class WallpaperManifest(val wallpapers: List<Wallpaper>)

enum class WallpaperQuality(
    val pathSegment: String,
    val width: Int,
    val height: Int,
    val displayName: String,
    val badge: String,
) {
    SD_480("480p", 480, 854, "480p Standard", "480p"),
    UHD_4K("4k", 2160, 3840, "4K Ultra HD", "4K"),
}

object CatalogConfig {
    /**
     * Public base URL of the generated wallpapers, laid out as
     * `<BASE_URL>/4k/<id>.jpg` and `<BASE_URL>/480p/<id>.jpg`
     * (the structure produced by AIWallpapers/tools/generate_wallpapers.py).
     */
    private const val BASE_URL = "https://cdn.example.com/wallpapers"

    /**
     * While true the app serves deterministic placeholder images so it is
     * fully browsable before the AI generation pipeline has been run and
     * uploaded. Set to false once BASE_URL points at the real images.
     */
    const val DEMO_MODE = true

    fun imageUrl(id: String, quality: WallpaperQuality): String =
        if (DEMO_MODE) {
            "https://picsum.photos/seed/$id/${quality.width}/${quality.height}"
        } else {
            "$BASE_URL/${quality.pathSegment}/$id.jpg"
        }
}
