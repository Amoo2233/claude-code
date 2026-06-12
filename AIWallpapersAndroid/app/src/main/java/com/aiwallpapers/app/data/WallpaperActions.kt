package com.aiwallpapers.app.data

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.aiwallpapers.app.model.Wallpaper
import com.aiwallpapers.app.model.WallpaperQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Downloads a wallpaper at the requested quality, then either saves the
 * original JPEG bytes to the gallery (no recompression, so 4K stays 4K)
 * or applies it as the device wallpaper.
 */
class WallpaperActions(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun saveToGallery(wallpaper: Wallpaper, quality: WallpaperQuality) {
        val bytes = download(wallpaper.imageUrl(quality))
        withContext(Dispatchers.IO) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME,
                    "${wallpaper.id}-${quality.badge}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AI Wallpapers")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            ) ?: error("Could not create gallery entry")
            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                ?: error("Could not write image")
        }
    }

    suspend fun setAsWallpaper(wallpaper: Wallpaper, quality: WallpaperQuality) {
        val bytes = download(wallpaper.imageUrl(quality))
        withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: error("Could not decode image")
            WallpaperManager.getInstance(context).setBitmap(bitmap)
        }
    }

    private suspend fun download(url: String): ByteArray = withContext(Dispatchers.IO) {
        client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            check(response.isSuccessful) { "HTTP ${response.code}" }
            response.body?.bytes() ?: error("Empty response")
        }
    }
}
