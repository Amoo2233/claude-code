package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.Wallpaper

private const val CARD_ASPECT_RATIO = 0.68f
private val CardShape = RoundedCornerShape(16.dp)

/**
 * Creates an elegant, luxury theme shimmering brush effect for images in transition loading states.
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1500f,
    showShimmer: Boolean = true
): Brush {
    if (!showShimmer) {
        return Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val shimmerColors = listOf(
        Color(0xFF121318),
        Color(0xFF232530),
        Color(0xFF2D303E),
        Color(0xFF232530),
        Color(0xFF121318)
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        // Keep the end point strictly ahead of the start so the first frame
        // (when translateAnimation == 0f) is not a degenerate, single-point gradient.
        end = Offset(x = translateAnimation + 1f, y = translateAnimation + 1f)
    )
}

/**
 * A beautiful, highly-polished shimmer placeholder card item representing offline skeleton cells.
 *
 * The default modifier sizes the card for use as a standalone grid cell. Callers that render the
 * placeholder inside an already-sized slot (e.g. the image loading state) can pass their own
 * sizing modifier such as [Modifier.fillMaxSize].
 */
@Composable
fun ShimmerPlaceholderCard(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(CARD_ASPECT_RATIO)
) {
    val brush = shimmerBrush()
    Box(
        modifier = modifier
            .clip(CardShape)
            .background(brush)
            .border(1.dp, Color(0xFF1A1B23), CardShape)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = Color(0xFF333544).copy(alpha = 0.6f),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
        )
    }
}

/**
 * Responsive grid card representing an individual luxury digital asset wallpaper.
 */
@Composable
fun ResponsiveWallpaperCard(
    wallpaper: Wallpaper,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onWallpaperClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRequest = remember(wallpaper.standardUrl, context) {
        ImageRequest.Builder(context)
            .data(wallpaper.standardUrl)
            .crossfade(true)
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(CARD_ASPECT_RATIO)
            .clip(CardShape)
            .background(Color(0xFF15161D))
            .border(1.dp, Color(0xFF22242E), CardShape)
            .clickable(onClick = onWallpaperClick)
            .testTag("wallpaper_item_${wallpaper.id}")
    ) {
        SubcomposeAsyncImage(
            model = imageRequest,
            loading = {
                ShimmerPlaceholderCard(modifier = Modifier.fillMaxSize())
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF22242E), Color(0xFF101115))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Network error",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Unreachable", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            },
            contentDescription = wallpaper.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        if (wallpaper.isPremium) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE9C46A), Color(0xFFD4AF37))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "VIP Premium",
                    tint = Color.Black,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "4K VIP",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wallpaper.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "by ${wallpaper.photographer}",
                    color = Color.LightGray.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .testTag("favorite_toggle_${wallpaper.id}")
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorited) "Remove Favorite" else "Add Favorite",
                    tint = if (isFavorited) Color(0xFFE76F51) else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Highly responsive image grid component supporting dynamic grid adaptation depending on
 * available window width, showing placeholder shimmers or a beautiful scrollable list of wallpapers.
 */
@Composable
fun ResponsiveWallpaperGrid(
    wallpapers: List<Wallpaper>,
    favoriteIds: Set<Int>,
    onFavoriteClick: (Wallpaper) -> Unit,
    onWallpaperClick: (Wallpaper) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    testTag: String = "responsive_wallpapers_grid"
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Calculate dynamic columns depending on screens/viewport classes
    val columns = when {
        screenWidth >= 1200.dp -> GridCells.Fixed(6)  // Large desktop/TV displays
        screenWidth >= 900.dp -> GridCells.Fixed(4)   // Large tablets/notebook foldables
        screenWidth >= 600.dp -> GridCells.Fixed(3)   // Standard landscape tablets
        else -> GridCells.Fixed(2)                     // Compact portrait phones
    }

    if (isLoading) {
        LazyVerticalGrid(
            columns = columns,
            modifier = modifier
                .fillMaxSize()
                .testTag("shimmer_loading_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(12) {
                ShimmerPlaceholderCard()
            }
        }
    } else {
        LazyVerticalGrid(
            columns = columns,
            modifier = modifier
                .fillMaxSize()
                .testTag(testTag),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(wallpapers, key = { it.id }) { wallpaper ->
                val isFavorited = favoriteIds.contains(wallpaper.id)
                val onFavorite = remember(wallpaper, onFavoriteClick) {
                    { onFavoriteClick(wallpaper) }
                }
                val onClick = remember(wallpaper, onWallpaperClick) {
                    { onWallpaperClick(wallpaper) }
                }
                ResponsiveWallpaperCard(
                    wallpaper = wallpaper,
                    isFavorited = isFavorited,
                    onFavoriteClick = onFavorite,
                    onWallpaperClick = onClick
                )
            }
        }
    }
}
