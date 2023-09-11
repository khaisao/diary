package com.sutech.diary.billding

import com.android.billingclient.api.ProductDetails
import com.sutech.diary.billding.ProductType

data class ProductModel(
    val productDetails:ProductDetails,
    val type: ProductType,
    val priceAmountMicros: Long,
    val currencyCode:String,
    val basePlanId:String? = null,
    val offerToken:String? = null
)