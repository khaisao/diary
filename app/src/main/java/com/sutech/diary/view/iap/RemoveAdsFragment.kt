package com.sutech.diary.view.iap

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.billding.BillingError
import com.sutech.diary.billding.BillingListener
import com.sutech.diary.billding.BillingModel
import com.sutech.diary.billding.ProductType
import com.sutech.diary.billding.SDKBilling
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.setPreventDoubleClickScaleView
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_remove_ads.*

class RemoveAdsFragment : BaseFragment(R.layout.fragment_remove_ads) {

    private val sdkBilling by lazy {
        SDKBilling(
            requireContext(), arrayListOf(
                BillingModel(mutableListOf(AppUtil.SKU_FOREVER), ProductType.INAPP)
            )
        )
    }

    private lateinit var billingClient: BillingClient


    override fun onDestroy() {
        sdkBilling.endConnection()
        super.onDestroy()
    }

    override fun initView() {
        logEvent("IAPScr_Show")
        tvBuyNow.setPreventDoubleClickScaleView(5000) {
            logEvent("IAPScr_BuyNow_Clicked")
            if (!context?.let { haveNetworkConnection(it) }!!) {
                Toast.makeText(context, R.string.check_internet, Toast.LENGTH_SHORT).show()
                return@setPreventDoubleClickScaleView
            }
            val buyNow = sdkBilling.buyNow(requireActivity(), AppUtil.SKU_FOREVER)
        }

        ivBack.setPreventDoubleClickScaleView {
            logEvent("IAPScr_ButtonX_Clicked")
            onBackPress(R.id.IAPFragment)
        }
        sdkBilling.listener = object : BillingListener {
            override fun onBillingPurchased(productId: String, purchase: Purchase) {
                AppUtil.isIAP = true
            }

            override fun onBillingSuccessfulPurchased(productId: String, purchase: Purchase) {
                AppUtil.isIAP = true
            }

            override fun onBillingError(billingError: BillingError) {
            }
        }
        sdkBilling.startConnection()
        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener(purchaseUpdateListener)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails()
                }
            }
            override fun onBillingServiceDisconnected() {
            }
        })
    }

    private fun querySkuDetails() {
        val skuList = listOf(AppUtil.SKU_FOREVER, AppUtil.SKU_FOREVER_FAKE)
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (skuDetailsList != null) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                    val skuDetailsReal = skuDetailsList[0]
                    val skuDetailsFake = skuDetailsList[1]
                    val priceReal = skuDetailsReal?.price
                    val priceFake = skuDetailsFake?.price

                    // Đây là giá của sản phẩm
                    // Bạn có thể hiển thị giá trong giao diện của bạn
                }
            }
        }
    }

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
            }
        }
    }

    private fun haveNetworkConnection(ctx: Context): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        return try {
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals(
                            "WIFI",
                            ignoreCase = true
                        )
                ) if (ni.isConnected) haveConnectedWifi = true
                if (ni.typeName.equals(
                            "MOBILE",
                            ignoreCase = true
                        )
                ) if (ni.isConnected) haveConnectedMobile = true
            }
            haveConnectedWifi || haveConnectedMobile
        } catch (e: java.lang.Exception) {
            System.err.println(e.toString())
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        billingClient.endConnection()
    }
}