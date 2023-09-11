package com.sutech.ads.fan.ads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.sutech.ads.AdCallback
import com.sutech.ads.model.AdsChild
import java.util.*

abstract class FanAds {
    protected var timeLoader = 0L
    abstract fun isDestroy(): Boolean
    abstract fun isLoaded(): Boolean
    abstract fun destroy()
    abstract fun loadAndShow(
        activity: Activity,
        adsChild: AdsChild,
        loadingText: String? ,
        layout: ViewGroup? ,
        layoutAds: View? ,
        lifecycle: Lifecycle? ,
        timeMillisecond: Long?,
        adCallback: AdCallback?
    )
    abstract fun preload(activity: Activity, adsChild: AdsChild)
    abstract fun show(
        activity: Activity,
        adsChild: AdsChild,
        loadingText: String? ,
        layout: ViewGroup? ,
        layoutAds: View? ,
        adCallback: AdCallback?
    ): Boolean
    public fun wasLoadTimeLessThanNHoursAgo(numHours: Long = 1): Boolean {
        val dateDifference: Long = Date().time - timeLoader
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
}