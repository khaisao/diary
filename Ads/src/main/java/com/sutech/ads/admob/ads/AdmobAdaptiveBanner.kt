package com.sutech.ads.admob.ads

import android.app.Activity
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.*
import com.sutech.ads.AdCallback
import com.sutech.ads.PreloadCallback
import com.sutech.ads.model.AdsChild
import com.sutech.ads.utils.AdDef
import com.sutech.ads.utils.Constant
import com.sutech.ads.utils.StateLoadAd
import com.sutech.ads.utils.Utils
import java.util.*


class AdmobAdaptiveBanner : AdmobAds() {
    private var isLoadSuccess = false
    private var adView: AdView? = null
    private var callback: AdCallback? = null
    private var callbackPreload:PreloadCallback? = null
    private var stateLoadAd = StateLoadAd.NONE
    override fun loadAndShow(
        activity: Activity,
        adsChild: AdsChild,
        loadingText: String?,
        layout: ViewGroup?,
        layoutAds: View?,
        lifecycle: Lifecycle?,
        timeMillisecond: Long?,
        adCallback: AdCallback?
    ) {
        callback = adCallback
        load(activity, adsChild, layout, callback, loadSuccess = {
            show(activity, adsChild, loadingText, layout, layoutAds,lifecycle ,callback)
        })
    }

    override fun show(
        activity: Activity,
        adsChild: AdsChild,
        loadingText: String?,
        layout: ViewGroup?,
        layoutAds: View?,
        lifecycle: Lifecycle?,
        adCallback: AdCallback?
    ): Boolean {
        callback = adCallback
        if (adView != null && layout != null) {
            try {
                adView?.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Utils.showToastDebug(activity, "Admob AdapBanner: ${adsChild.adsId}")
                        callback?.onAdClick()
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        callback?.onAdClose(AdDef.NETWORK.GOOGLE)
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Utils.showToastDebug(activity, "Admob AdapBanner: ${p0?.message}")
                        callback?.onAdFailToLoad(p0?.message)
                    }


                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isLoadSuccess = true

                        timeLoader = Date().time
                    }
                }
                callback?.onAdShow(AdDef.NETWORK.GOOGLE, AdDef.ADS_TYPE.BANNER)
                layout.removeAllViews()
                if (adView!!.parent != null) {
                    (adView!!.parent as ViewGroup).removeView(adView) // <- fix
                }
                layout.addView(adView)
            } catch (e: Exception) {
            }

            return true
        } else {
            Utils.showToastDebug(activity, "layout ad native not null")
        }
        return false
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        callbackPreload = preloadCallback
    }

    override fun preload(activity: Activity, adsChild: AdsChild) {
        load(activity, adsChild, null,null, loadSuccess = {

        })
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        layout: ViewGroup?,
        adCallback: AdCallback?,
        loadSuccess: () -> Unit
    ) {
        callback = adCallback
        val id: String = if (Constant.isDebug) {
            Constant.ID_ADMOB_BANNER_TEST
        } else {
            adsChild.adsId
        }
        stateLoadAd = StateLoadAd.LOADING
        isLoadSuccess = false
        adView = AdView(activity.applicationContext)
        adView?.setBackgroundColor(Color.WHITE)
        adView?.adUnitId = id


        val adSize = getAdsize(activity)
        adSize?.let {
            adView?.setAdSize(it)
        }

        layout?.let { viewG ->
            val lp = viewG.layoutParams
            lp.width = adSize?.getWidthInPixels(viewG.context)?: 0
            lp.height = adSize?.getHeightInPixels(viewG.context) ?: 0
            viewG.layoutParams = lp
        }


        adView?.loadAd(
            AdRequest.Builder().build()
        )
        adView?.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Utils.showToastDebug(activity, "Admob AdapBanner: ${adsChild.adsId}")
                callback?.onAdClick()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                callback?.onAdClose(AdDef.NETWORK.GOOGLE)
            }


            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Utils.showToastDebug(activity, "Admob AdapBanner: ${p0?.message}")
                callback?.onAdFailToLoad(p0?.message)
                stateLoadAd = StateLoadAd.FAILED
                callbackPreload?.onLoadFail()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                stateLoadAd = StateLoadAd.SUCCESS
                isLoadSuccess = true
                callbackPreload?.onLoadDone()
                loadSuccess()
                timeLoader = Date().time
            }
        }

    }

    private fun getAdsize(activity: Activity): AdSize? {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity,
            adWidth
        )
    }

    override fun destroy() {
        adView = null
        isLoadSuccess = false
    }

    override fun isDestroy(): Boolean {
        return adView == null
    }

    override fun isLoaded(): Boolean {
        return isLoadSuccess
    }

    override fun getStateLoadAd(): StateLoadAd {
        return stateLoadAd
    }

}