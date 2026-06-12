package com.aiwallpapers.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aiwallpapers.app.billing.PremiumManager
import com.aiwallpapers.app.data.FavoritesStore
import com.aiwallpapers.app.data.WallpaperActions
import com.aiwallpapers.app.model.Wallpaper
import kotlinx.coroutines.launch

private sealed interface ActionState {
    data object Idle : ActionState
    data object Working : ActionState
    data class Done(val message: String) : ActionState
    data class Failed(val message: String) : ActionState
}

@Composable
fun DetailScreen(
    wallpaper: Wallpaper,
    premium: PremiumManager,
    favorites: FavoritesStore,
    actions: WallpaperActions,
    navController: NavController,
) {
    val isPremium by premium.isPremium.collectAsState()
    val favoriteIds by favorites.ids.collectAsState()
    val quality = premium.maxQuality
    var state by remember { mutableStateOf<ActionState>(ActionState.Idle) }
    val scope = rememberCoroutineScope()

    fun run(label: String, block: suspend () -> Unit) {
        scope.launch {
            state = ActionState.Working
            state = try {
                block()
                ActionState.Done(label)
            } catch (e: Exception) {
                ActionState.Failed("Failed — check your connection and try again.")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = wallpaper.imageUrl(quality),
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text(wallpaper.title,
                    style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(wallpaper.category,
                    style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
            IconButton(onClick = { favorites.toggle(wallpaper.id) }) {
                val isFavorite = wallpaper.id in favoriteIds
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    quality.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isPremium) Color.Black else Color.White,
                    modifier = Modifier
                        .background(
                            if (isPremium) Color(0xFFFFD54F) else Color.DarkGray,
                            RoundedCornerShape(50),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
                Spacer(Modifier.weight(1f))
                if (!isPremium) {
                    Button(
                        onClick = { navController.navigate("paywall") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD54F),
                            contentColor = Color.Black,
                        ),
                    ) {
                        Icon(Icons.Default.WorkspacePremium, null)
                        Text(" Unlock 4K")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        run("Saved to gallery in ${quality.badge}") {
                            actions.saveToGallery(wallpaper, quality)
                        }
                    },
                    enabled = state != ActionState.Working,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Download, null)
                    Text(" Download ${quality.badge}")
                }
                OutlinedButton(
                    onClick = {
                        run("Wallpaper applied") {
                            actions.setAsWallpaper(wallpaper, quality)
                        }
                    },
                    enabled = state != ActionState.Working,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Wallpaper, null)
                    Text(" Set")
                }
            }

            when (val s = state) {
                is ActionState.Working -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    Text("Working…", color = Color.White)
                }
                is ActionState.Done -> Text(s.message, color = Color(0xFF81C784))
                is ActionState.Failed -> Text(s.message, color = Color(0xFFE57373))
                ActionState.Idle -> Unit
            }
        }
    }
}
