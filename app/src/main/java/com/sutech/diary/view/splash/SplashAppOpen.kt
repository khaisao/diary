package com.sutech.diary.view.splash

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class SplashAppOpen(private val context: Context) {
    private var admobAppOpenAd: AppOpenAd? = null
    private var unitId = ""
    var listener: SplashAppOpenListener? = null
    var isShowing = false
    private var isError = false
    private var startLoadAd = false

    fun setAdsId(adIdAdmob: String) {
        this.unitId = adIdAdmob
    }

    fun isLoaded(): Boolean {
        return admobAppOpenAd != null
    }

    fun isError(): Boolean {
        return isError
    }

    fun loadAd() {
        if (isLoaded() || startLoadAd) return
        startLoadAd = true
        requestAd()
    }

    fun showAd(activity: Activity) {
        if (isShowing) return
        if (isLoaded()) {
            mShowAd(activity, admobAppOpenAd) { clearAdmobAd() }
        } else listener?.onAdDismissedFullScreen()
    }

    fun destroyAd() {
        clearAdmobAd()
    }

    private fun clearAdmobAd() {
        admobAppOpenAd?.fullScreenContentCallback = null
        admobAppOpenAd = null
        isShowing = false
    }

    private fun mShowAd(activity: Activity, ad: AppOpenAd?, callback: (() -> Unit)?) {
        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                isShowing = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                isShowing = false
                callback?.let { it() }
                listener?.onAdDismissedFullScreen()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                isShowing = false
                callback?.let { it() }
                listener?.onAdDismissedFullScreen()
            }
        }
        ad?.show(activity)
    }

    private fun requestAd() {
        startLoadAd = true
        isError = false
        AppOpenAd.load(context,
            unitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    admobAppOpenAd = p0
                    startLoadAd = false
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    startLoadAd = false
                    isError = true
                }
            })
    }
}