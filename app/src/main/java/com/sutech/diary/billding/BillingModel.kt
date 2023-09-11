package com.sutech.diary.billding

import com.android.billingclient.api.BillingClient

enum class ProductType(val type: String) {
    SUBS(BillingClient.ProductType.SUBS),
    INAPP(BillingClient.ProductType.INAPP)
}

data class BillingModel(
    val productIDList: MutableList<String>,
    val productType: ProductType
)