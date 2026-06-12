package com.aiwallpapers.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aiwallpapers.app.billing.PremiumManager
import com.aiwallpapers.app.data.FavoritesStore
import com.aiwallpapers.app.data.WallpaperActions
import com.aiwallpapers.app.data.WallpaperCatalog
import com.aiwallpapers.app.ui.AppRoot
import com.aiwallpapers.app.ui.theme.AIWallpapersTheme

class MainActivity : ComponentActivity() {

    private lateinit var premium: PremiumManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        premium = PremiumManager(applicationContext)
        premium.connect()
        val catalog = WallpaperCatalog(applicationContext)
        val favorites = FavoritesStore(applicationContext)
        val actions = WallpaperActions(applicationContext)

        setContent {
            AIWallpapersTheme {
                AppRoot(
                    catalog = catalog,
                    premium = premium,
                    favorites = favorites,
                    actions = actions,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Pick up purchases completed outside the app (e.g. Play Store).
        premium.refresh()
    }
}
