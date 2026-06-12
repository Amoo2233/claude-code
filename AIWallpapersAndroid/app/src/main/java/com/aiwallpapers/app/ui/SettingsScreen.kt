package com.aiwallpapers.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aiwallpapers.app.billing.PremiumManager
import com.aiwallpapers.app.data.WallpaperCatalog

@Composable
fun SettingsScreen(
    catalog: WallpaperCatalog,
    premium: PremiumManager,
    navController: NavController,
) {
    val isPremium by premium.isPremium.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("Membership", style = MaterialTheme.typography.titleMedium)
        SettingRow("Plan", if (isPremium) "Premium" else "Free")
        SettingRow("Download quality", premium.maxQuality.displayName)
        if (!isPremium) {
            Button(
                onClick = { navController.navigate("paywall") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Text("Upgrade to 4K Premium")
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp))

        Text("Catalog", style = MaterialTheme.typography.titleMedium)
        SettingRow("Wallpapers", catalog.wallpapers.size.toString())
        SettingRow("Categories", catalog.categories.size.toString())
        SettingRow("Source", "AI generated")

        HorizontalDivider(Modifier.padding(vertical = 16.dp))

        Text(
            "All wallpapers are generated with AI. Premium unlocks 4K Ultra HD " +
                "downloads; the free plan includes every wallpaper in 480p. " +
                "Purchases are restored automatically from your Google account.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
