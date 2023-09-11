package com.sutech.ads

import android.os.Bundle

interface AdCallback {

    fun onAdShow(network: String, adtype: String)
    fun onAdClose(adType: String)
    fun onAdFailToLoad(messageError: String?)
    fun onAdOff()
    fun onAdClick(){}
    fun onPaidEvent(params: Bundle) {}
    fun onRewardShow(network: String, adtype: String) {}

}