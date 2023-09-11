package com.sutech.ads.admob.ads

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sutech.ads.AdCallback
import com.sutech.ads.AdsController
import com.sutech.ads.PreloadCallback
import com.sutech.ads.StateADCallback
import com.sutech.ads.model.AdsChild
import com.sutech.ads.utils.*
import java.util.*

class AdmobInterstitial : AdmobAds() {
    private var error: String? = null

    private var loadFailed = false
    private var loaded: Boolean = false
    private var preload: Boolean = false
    private var isTimeOut = false
    private var handler = Handler(Looper.getMainLooper())
    private var eventLifecycle: Lifecycle.Event = Lifecycle.Event.ON_RESUME
    private var mInterstitialAd: InterstitialAd? = null
    private var timeClick = 0L
    private var callback: AdCallback? = null
    private val TAG = "AdmobInterstitial"
    private var currentActivity: Activity? = null
    private var lifecycle: Lifecycle? = null
    private var callbackPreload: PreloadCallback? = null
    private var stateLoadAd = StateLoadAd.NONE
    private var isloading = false
    private fun resetValue() {
        loaded = false
        loadFailed = false
        error = null
    }

    override fun setPreloadCallback(preloadCallback: PreloadCallback?) {
        callbackPreload = preloadCallback
    }

    fun setStateAdCallback(stateADCallback: StateADCallback?) {
        stateADCallback?.onState(stateLoadAd)
    }


    override fun loadAndShow(
        activity: Activity,
        adsChild: AdsChild,
        textLoading: String?,
        layout: ViewGroup?,
        layoutAds: View?,
        lifecycle: Lifecycle?,
        timeMillisecond: Long?,
        adCallback: AdCallback?
    ) {
        currentActivity = activity
        callback = adCallback
        preload = false
        load(
            activity,
            adsChild,
            textLoading,
            lifecycle,
            timeMillisecond ?: Constant.TIME_OUT_DEFAULT,
            adCallback
        )
        AdDialog.getInstance().showLoadingWithMessage(activity, textLoading)
    }

    override fun preload(activity: Activity, adsChild: AdsChild) {
        preload = true
        currentActivity = activity
        load(activity, adsChild, null, null, Constant.TIME_OUT_DEFAULT, null)
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
        currentActivity = activity
        if (lifecycle != null) {
            lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        lifecycle?.removeObserver(this)
                        AdDialog.getInstance().showLoadingWithMessage(activity, loadingText)
                        if (loaded && mInterstitialAd != null) {
                            mInterstitialAd?.show(activity)
                            stateLoadAd = StateLoadAd.NONE
                        }
                    }
                }
            })
        } else {
            AdDialog.getInstance().showLoadingWithMessage(activity, loadingText)
            if (loaded && mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
                stateLoadAd = StateLoadAd.NONE
            }
        }
        return true
    }


    private val timeOutCallBack = Runnable {
        if (!loaded && !loadFailed) {
            isTimeOut = true
            if (eventLifecycle == Lifecycle.Event.ON_RESUME) {
                AdDialog.getInstance().hideLoading()
                callback?.onAdFailToLoad("TimeOut")
                stateLoadAd = StateLoadAd.FAILED
                lifecycle?.removeObserver(lifecycleObserver)
            }
        }
    }

    private fun load(
        activity: Activity,
        adsChild: AdsChild,
        textLoading: String?,
        lifecycle: Lifecycle?,
        timeOut: Long,
        adCallback: AdCallback?
    ) {
        isloading = true
        stateLoadAd = StateLoadAd.LOADING
        if (System.currentTimeMillis() - timeClick < 500) return
        textLoading?.let {
            AdDialog.getInstance().showLoadingWithMessage(activity, textLoading)
        }
        Log.d(TAG, "load: " + textLoading)
        Utils.showToastDebug(activity, "Admob Interstitial id: ${adsChild.adsId}")

        if (!preload) {
            lifecycle?.addObserver(lifecycleObserver)
            handler.removeCallbacks(timeOutCallBack)
            handler.postDelayed(timeOutCallBack, timeOut)
        }
        resetValue()
        this.callback = adCallback
        this.lifecycle = lifecycle
        timeClick = System.currentTimeMillis()
        val id = if (Constant.isDebug) Constant.ID_ADMOB_INTERSTITIAL_TEST else adsChild.adsId
        val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                stateLoadAd = StateLoadAd.SUCCESS
                mInterstitialAd = p0
                mInterstitialAd?.setOnPaidEventListener {
                    kotlin.runCatching {
                        val params = Bundle()
                        params.putString("valuemicros", it.valueMicros.toString())
                        params.putString("currency", it.currencyCode)
                        params.putString("precision", it.precisionType.toString())
                        params.putString("adunitid", p0.adUnitId)
                        params.putString("network", p0.responseInfo.mediationAdapterClassName)
                        callback?.onPaidEvent(params)
                    }
                }
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            Log.d(TAG, "onAdDismissedFullScreenContent: ")
                            callback?.onAdClose(AdDef.ADS_TYPE.INTERSTITIAL)
                            mInterstitialAd = null

                            //// perform your code that you wants to do after ad dismissed or closed
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            Log.d(TAG, "onAdFailedToShowFullScreenContent: $adError")
                            mInterstitialAd = null
                            loadFailed = true
                            error = adError.message
                            if (eventLifecycle == Lifecycle.Event.ON_RESUME && !preload && !isTimeOut) {
                                AdDialog.getInstance().hideLoading()
                                callback?.onAdFailToLoad(adError.message)
                                lifecycle?.removeObserver(lifecycleObserver)
                            }
                            /// perform your action here when ad will not load
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            Log.d(TAG, "onAdShowedFullScreenContent: ")
                            mInterstitialAd = null
                            stateLoadAd = StateLoadAd.HAS_BEEN_OPENED
                            AdDialog.getInstance().hideLoading()
                            Utils.showToastDebug(
                                activity,
                                "Admob Interstitial id: ${adsChild.adsId}"
                            )
                            callback?.onAdShow(
                                AdDef.NETWORK.GOOGLE,
                                AdDef.ADS_TYPE.INTERSTITIAL
                            )
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            if (AdsController.mTopActivity != null && AdsController.mTopActivity is AdActivity) {
                                AdsController.mTopActivity?.finish()
                            }
                            callback?.onAdClick()
                        }
                    }
                if (eventLifecycle == Lifecycle.Event.ON_RESUME && !preload && !isTimeOut) {
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        AdDialog.getInstance().hideLoading()
                    }, 500)
                    currentActivity?.let { mInterstitialAd?.show(it) }
                    lifecycle?.removeObserver(lifecycleObserver)
                }
                loaded = true
                timeLoader = Date().time
                Log.d(TAG, "onAdLoaded: ")
                callbackPreload?.onLoadDone()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                stateLoadAd = StateLoadAd.FAILED
                loadFailed = true
                error = p0.message
                if (eventLifecycle == Lifecycle.Event.ON_RESUME && !preload && !isTimeOut) {
                    AdDialog.getInstance().hideLoading()
                    callback?.onAdFailToLoad(p0.message)
                    lifecycle?.removeObserver(lifecycleObserver)
                }
                callbackPreload?.onLoadFail()
            }
        }
        InterstitialAd.load(
            activity,
            id,
            AdRequest.Builder().build(),
            interstitialAdLoadCallback
        )

    }

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            eventLifecycle = event
            if (event == Lifecycle.Event.ON_RESUME) {
                AdDialog.getInstance().hideLoading()
                if (isTimeOut) {
                    AdDialog.getInstance().hideLoading()
                    callback?.onAdFailToLoad("TimeOut")
                    stateLoadAd = StateLoadAd.FAILED
                    lifecycle?.removeObserver(this)
                } else if (loadFailed || loaded) {
                    AdDialog.getInstance().hideLoading()
                    if (loaded) {
                        currentActivity?.let { mInterstitialAd?.show(it) }
                    } else {
                        stateLoadAd = StateLoadAd.FAILED
                        callback?.onAdFailToLoad(error)
                    }
                    lifecycle?.removeObserver(this)
                }
            }
        }
    }

    override fun isLoaded(): Boolean {
        return loaded
    }

    override fun getStateLoadAd(): StateLoadAd {
        return stateLoadAd
    }

    override fun isDestroy(): Boolean {
        return mInterstitialAd == null
    }

    override fun destroy() {
        isTimeOut = true
        mInterstitialAd = null
    }

}

