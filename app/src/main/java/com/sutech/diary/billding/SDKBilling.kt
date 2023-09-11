package com.sutech.diary.billding

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import java.text.DecimalFormat

class SDKBilling(
    private val context: Context, private val productList: ArrayList<BillingModel>
) : BillingClientStateListener {

    companion object {
        fun convertPrice(price: Float, currency: String): String {
            val formatter = if (currency.uppercase() == "₫") {
                DecimalFormat("#,##0")
            } else {
                DecimalFormat("#,##0.00")
            }
            return formatter.format(price)
        }
    }

    private var mPurchaseList = mutableListOf<Purchase>()
    private var products = mutableListOf<ProductModel>()
    private var isInAppQueryPurchasesAsync = false
    private var isSubQueryPurchasesAsync = false
    var listener: BillingListener? = null

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context).setListener { billingResult, purchases ->
            checkBillingResult(billingResult, purchases)
        }.enablePendingPurchases().build()
    }

    fun startConnection() {
        billingClient.startConnection(this)
    }

    fun endConnection() {
        listener = null
        billingClient.endConnection()
    }

    override fun onBillingServiceDisconnected() {
        handler.post { listener?.onBillingError(BillingError.DISCONNECTED) }
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        val inApp = productList.firstOrNull { it.productType == ProductType.INAPP }
        val sub = productList.firstOrNull { it.productType == ProductType.SUBS }
        if (inApp != null) {
            mQueryPurchasesAsync(ProductType.INAPP, inApp) {
                isInAppQueryPurchasesAsync = true
                if (isSubQueryPurchasesAsync) checkPurchase()
            }
        } else isInAppQueryPurchasesAsync = true
        if (sub != null) {
            mQueryPurchasesAsync(ProductType.SUBS, sub) {
                isSubQueryPurchasesAsync = true
                if (isInAppQueryPurchasesAsync) checkPurchase()
            }
        } else isSubQueryPurchasesAsync = true
    }

    private fun mQueryPurchasesAsync(
        productType: ProductType, productModel: BillingModel, complete: (() -> Unit)? = null
    ) {
        val productIDList = productModel.productIDList
        if (productIDList.isEmpty()) {
            complete?.let { it() }
            return
        }
        val newList = mutableListOf<QueryProductDetailsParams.Product>()
        for (productId in productIDList) {
            if (productId.isEmpty()) continue
            newList.add(
                QueryProductDetailsParams.Product.newBuilder().setProductId(productId)
                    .setProductType(ProductType::type.get(productModel.productType)).build()
            )
        }
        val purchasesParams =
            QueryPurchasesParams.newBuilder().setProductType(ProductType::type.get(productType))
                .build()

        val detailsParams = QueryProductDetailsParams.newBuilder().setProductList(newList).build()
        billingClient.queryPurchasesAsync(purchasesParams) { _, purchaseList ->
            if (purchaseList.isNotEmpty()) {
                mPurchaseList.addAll(purchaseList)
            }
            billingClient.queryProductDetailsAsync(detailsParams) { _, productDetailsList ->
                if (productDetailsList.isNotEmpty()) {
                    for (product in productDetailsList) {
                        val subscriptionOfferDetails = product.subscriptionOfferDetails
                        if (subscriptionOfferDetails != null) {
                            subscriptionOfferDetails.forEach {
                                val pricingPhases = it.pricingPhases
                                val pricingPhaseList = pricingPhases.pricingPhaseList
                                if (pricingPhaseList.isNotEmpty()) {
                                    val offer = pricingPhaseList.first()
                                    val formattedPrice = offer.formattedPrice
                                    val priceCode = if (formattedPrice.length > 1) {
                                        formattedPrice[if (Character.isDigit(formattedPrice[0])) formattedPrice.length - 1 else 0].toString()
                                    } else {
                                        offer.priceCurrencyCode
                                    }
                                    val newProduct = ProductModel(
                                        product,
                                        productType,
                                        offer.priceAmountMicros,
                                        priceCode,
                                        it.basePlanId,
                                        it.offerToken
                                    )
                                    products.add(newProduct)
                                    handler.post {
                                        listener?.onBillingPrice(newProduct)
                                    }
                                }
                            }
                        } else {
                            val details = product.oneTimePurchaseOfferDetails
                            if (details != null) {
                                val formattedPrice = details.formattedPrice
                                val priceCode = if (formattedPrice.length > 1) {
                                    formattedPrice[if (Character.isDigit(formattedPrice[0])) formattedPrice.length - 1 else 0].toString()
                                } else {
                                    details.priceCurrencyCode
                                }
                                val newProduct = ProductModel(
                                    product,
                                    productType,
                                    details.priceAmountMicros,
                                    priceCode,
                                )
                                products.add(newProduct)
                                handler.post {
                                    listener?.onBillingPrice(newProduct)
                                }
                            }
                        }
                    }
                }
                complete?.let { it() }
            }
        }
    }

    private fun checkBillingResult(
        billingResult: BillingResult, list: List<Purchase?>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (list != null) {
                for (purchase in list) {
                    purchase ?: continue
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) {
            checkPurchased(purchase)
            return
        }
        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                checkPurchased(purchase)
            }
        }
    }

    private fun checkPurchased(purchase: Purchase) {
        val products = purchase.products
        if (products.isNotEmpty()) {
            handler.post { listener?.onBillingSuccessfulPurchased(products.first(), purchase) }
        }
    }

    private fun checkPurchase() {
        handler.post {
            listener?.onBillingStartCheckPurchase()
        }
        if (mPurchaseList.isNotEmpty()) {
            for (purchase in mPurchaseList) {
                val products = purchase.products
                if (products.isNotEmpty()) {
                    handler.post {
                        listener?.onBillingPurchased(
                            products.first(), purchase
                        )
                    }
                }
            }
        }
        handler.post {
            listener?.onBillingCompleteCheckPurchase(products.isEmpty())
        }
    }

    fun buyOfferNow(activity: Activity, basePlanId: String): Boolean {
        val product = products.firstOrNull { it.basePlanId == basePlanId } ?: return false
        val productDetailsParamsList = listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product.productDetails).apply {
                setOfferToken(product.offerToken.orEmpty())
            }.build()
        )
        val billingFlowParams =
            BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
        return true
    }

    fun buyNow(activity: Activity, productId: String): Boolean {
        val product =
            products.firstOrNull { it.productDetails.productId == productId } ?: return false
        val productDetailsParamsList = listOf(BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product.productDetails).apply {
                setOfferToken(product.offerToken.orEmpty())
            }.build()
        )
        val billingFlowParams =
            BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
        return true
    }

    fun consumeAsync(purchase: Purchase) {
        if (!BuildConfig.DEBUG) return
        val build = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(build) { _, _ -> }
    }
}