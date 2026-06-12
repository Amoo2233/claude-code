package com.aiwallpapers.app.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aiwallpapers.app.billing.PremiumManager
import com.android.billingclient.api.ProductDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(premium: PremiumManager, navController: NavController) {
    val isPremium by premium.isPremium.collectAsState()
    val monthly by premium.monthly.collectAsState()
    val lifetime by premium.lifetime.collectAsState()
    val activity = LocalContext.current as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                "Unlock 4K Ultra HD",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
            )

            Benefit(Icons.Default.HighQuality, "All 500 wallpapers in 4K (2160×3840)")
            Benefit(Icons.Default.AutoAwesome, "Every new AI drop included")
            Benefit(Icons.Default.AllInclusive, "Unlimited downloads")

            Spacer(Modifier.padding(4.dp))

            when {
                isPremium -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, null, tint = Color(0xFF81C784))
                    Text("  You're Premium — 4K unlocked",
                        style = MaterialTheme.typography.titleMedium)
                }
                monthly == null && lifetime == null -> Text(
                    "Products unavailable. Billing requires the app to be " +
                        "installed via Google Play (internal testing works).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                else -> {
                    monthly?.let { details ->
                        PurchaseButton(
                            title = "Premium Monthly",
                            price = details.subscriptionOfferDetails?.firstOrNull()
                                ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                                ?.formattedPrice?.let { "$it / month" } ?: "Subscribe",
                        ) { activity?.let { premium.launchPurchase(it, details) } }
                    }
                    lifetime?.let { details ->
                        PurchaseButton(
                            title = "Premium Lifetime",
                            price = details.oneTimePurchaseOfferDetails
                                ?.formattedPrice?.let { "$it one time" } ?: "Buy",
                        ) { activity?.let { premium.launchPurchase(it, details) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun Benefit(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFFD54F))
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
}

@Composable
private fun PurchaseButton(title: String, price: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD54F),
            contentColor = Color.Black,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(price, style = MaterialTheme.typography.bodySmall)
        }
    }
}
