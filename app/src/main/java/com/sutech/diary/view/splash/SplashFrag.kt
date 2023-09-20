package com.sutech.diary.view.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.Purchase
import com.sutech.ads.AdCallback
import com.sutech.ads.AdsController
import com.sutech.ads.PreloadCallback
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.billding.BillingError
import com.sutech.diary.billding.BillingListener
import com.sutech.diary.billding.BillingModel
import com.sutech.diary.billding.ProductModel
import com.sutech.diary.billding.ProductType
import com.sutech.diary.billding.SDKBilling
import com.sutech.diary.database.DataStore
import com.sutech.diary.database.DiaryDatabase
import com.sutech.diary.model.ContentModel
import com.sutech.diary.model.DiaryModel
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.Constant
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.gone
import com.sutech.diary.util.setPreventDoubleClickItem
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log
import kotlin.time.Duration.Companion.seconds

class SplashFrag : BaseFragment(R.layout.fragment_splash), SplashAppOpenListener, BillingListener,
    JobSplash.JobProgress {

    private val jobSplash by lazy { JobSplash() }
    private val sdkBilling by lazy {
        SDKBilling(
            requireContext(), arrayListOf(
                BillingModel(mutableListOf(AppUtil.SKU_FOREVER), ProductType.INAPP)
            )
        )
    }
    private val splashAppOpen by lazy { SplashAppOpen(requireContext()) }
    private var iapLoadSuccess = false
    private var isCheckLoadAd = false
    private var isShowAds = false
    private var diaryDataBase: DiaryDatabase? = null
    private var isGoHome = false

    override fun onResume() {
        if (iapLoadSuccess) jobSplash.startJob(this)
        super.onResume()
        if (isGoHome) {
            goHome()
        }
    }

    override fun onPause() {
        if (iapLoadSuccess) jobSplash.stopJob()
        super.onPause()
    }

    override fun onDestroy() {
        sdkBilling.endConnection()
        super.onDestroy()
    }

    override fun initView() {
        logEvent("SplashScr_Show")
        splashAppOpen.listener = this
        splashAppOpen.setAdsId("ca-app-pub-2094788208346877/6077331608")
        activity?.onBackPressedDispatcher?.addCallback(this, true) {

        }
        AppUtil.needUpdateDiary = true
        context?.let { ctx ->
            diaryDataBase = DiaryDatabase.getInstance(ctx)
        }
        ImageUtil.setImage(imgLogo, R.drawable.drawer_header)
        imgLogo?.setPreventDoubleClickItem {
            logEvent("AppStartup_IconApp_Clicked")
        }
        insertFirstDiary()
        lifecycleScope.launch {
            delay(5.seconds)
            if (!iapLoadSuccess) {
                checkLoadAds()
                iapLoadSuccess = true
            }
        }
        sdkBilling.listener = this
        sdkBilling.startConnection()
    }

    override fun onBillingStartCheckPurchase() {

    }

    override fun onBillingPurchased(productId: String, purchase: Purchase) {
        AppUtil.isIAP = true
    }

    override fun onBillingCompleteCheckPurchase(isError: Boolean) {
        Log.i("aaaaaaaaaaaa", "onBillingCompleteCheckPurchase")
        iapLoadSuccess = true
        checkLoadAds()
    }

    override fun onBillingPrice(product: ProductModel) {
        Log.i("aaaaaaaaaaaa", "onBillingPrice: ${product.productDetails.productId}")
    }

    override fun onProgress(count: Int) {
        if (isShowAds || !inForceGround) {
            return
        }
        pgSplash.progress = count
        if (splashAppOpen.isLoaded() && !splashAppOpen.isShowing && !isShowAds) {
            isShowAds = true
            jobSplash.stopJob()
            pgSplash.gone()
            splashAppOpen.showAd(requireActivity())
        } else if (!isShowAds && splashAppOpen.isError()) {
            isShowAds = true
            goHome()
        } else if (!isShowAds && jobSplash.isProgressMax()) {
            goHome()
        }
    }

    override fun onAdDismissedFullScreen() {
        AppUtil.checkInter = true
        goHome()
    }

    private fun checkLoadAds() {
        if (isCheckLoadAd) return
        isCheckLoadAd = true
        if (AppUtil.isIAP) {
            pgSplash.progress = 100
            goHome()
        } else {
            loadAd()
            if (inForceGround) jobSplash.startJob(this)
        }
    }

    private fun loadAd() {
        splashAppOpen.loadAd()
    }

    private fun insertFirstDiary() {
        CoroutineScope(Dispatchers.IO).launch {
            diaryDataBase?.getDiaryDao()?.getAllDiary()?.let {
                withContext(Dispatchers.Main) {
                    if (it.isEmpty()) {
                        val date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
                        val dateTime = SimpleDateFormat("EEEE dd-MM-yyyy", Locale.US).format(Date())
                        val diaryModel = DiaryModel(dateTime = date)
                        diaryModel.listContent = ArrayList()
                        diaryModel.listContent!!.add(
                            0, ContentModel(
                                title = getString(R.string.app_name),
                                content = getString(R.string.content_introduce),
                                images = ArrayList(),
                                createDate = date,
                                dateTimeCreate = dateTime
                            )
                        )
                        diaryDataBase?.getDiaryDao()?.insertDiary(diaryModel)?.let { }
                    }
                }
            }
        }
    }

    private fun goHome() {
        isGoHome = true
        if (DataStore.getFirstTimeOpenApp()) {
            gotoFrag(R.id.splashFrag, R.id.action_splashFrag_to_mainFrag)
            DataStore.setFirstTimeOpenApp(false)
        } else {
            if (DataStore.getUsePassword() && !DataStore.getPassword().isNullOrBlank()) {
                val bundle = Bundle()
                bundle.putInt(Constant.TYPE_PASSWORD, 3)
                gotoFrag(R.id.splashFrag, R.id.action_splashFrag_to_passWordFrag, bundle)
            }  else {
                gotoFrag(R.id.splashFrag, R.id.action_splashFrag_to_mainFrag)
            }
        }
    }
}