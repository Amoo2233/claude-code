package com.aiwallpapers.app.billing

import android.app.Activity
import android.content.Context
import com.aiwallpapers.app.model.WallpaperQuality
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Google Play Billing wrapper that gates 4K downloads. Free users get 480p;
 * an active monthly subscription or the lifetime unlock enables 4K Ultra HD.
 */
class PremiumManager(context: Context) : PurchasesUpdatedListener {

    companion object {
        const val MONTHLY_PRODUCT_ID = "premium_monthly"
        const val LIFETIME_PRODUCT_ID = "premium_lifetime"
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _monthly = MutableStateFlow<ProductDetails?>(null)
    val monthly: StateFlow<ProductDetails?> = _monthly.asStateFlow()

    private val _lifetime = MutableStateFlow<ProductDetails?>(null)
    val lifetime: StateFlow<ProductDetails?> = _lifetime.asStateFlow()

    val maxQuality: WallpaperQuality
        get() = if (_isPremium.value) WallpaperQuality.UHD_4K else WallpaperQuality.SD_480

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        loadProducts()
                        refreshEntitlements()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Play will rebind on the next connect(); entitlements are
                // re-checked every time the activity resumes.
            }
        })
    }

    fun launchPurchase(activity: Activity, details: ProductDetails) {
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .apply {
                details.subscriptionOfferDetails?.firstOrNull()
                    ?.let { setOfferToken(it.offerToken) }
            }
            .build()
        billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productParams))
                .build()
        )
    }

    fun refresh() {
        if (billingClient.isReady) {
            scope.launch { refreshEntitlements() }
        } else {
            connect()
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            scope.launch { refreshEntitlements() }
        }
    }

    private suspend fun loadProducts() {
        _monthly.value = queryProduct(MONTHLY_PRODUCT_ID, BillingClient.ProductType.SUBS)
        _lifetime.value = queryProduct(LIFETIME_PRODUCT_ID, BillingClient.ProductType.INAPP)
    }

    private suspend fun queryProduct(productId: String, type: String): ProductDetails? {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(type)
                        .build()
                )
            )
            .build()
        return billingClient.queryProductDetails(params).productDetailsList?.firstOrNull()
    }

    private suspend fun refreshEntitlements() {
        val purchases = queryPurchases(BillingClient.ProductType.SUBS) +
            queryPurchases(BillingClient.ProductType.INAPP)
        val active = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }

        active.filterNot { it.isAcknowledged }.forEach { purchase ->
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            )
        }

        _isPremium.value = active.any { purchase ->
            purchase.products.any { it == MONTHLY_PRODUCT_ID || it == LIFETIME_PRODUCT_ID }
        }
    }

    private suspend fun queryPurchases(type: String): List<Purchase> =
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(type).build()
        ).purchasesList
}
