package com.sutech.diary.base

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.sutech.ads.AdCallback
import com.sutech.ads.AdsController
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.gone
import com.sutech.journal.diary.diarywriting.lockdiary.R

abstract class BaseFragment(val layout: Int) : Fragment() {
    protected abstract fun initView()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)

    }

    override fun onPause() {
        inForceGround = false
        super.onPause()
    }

    override fun onResume() {
        inForceGround = true
        AppUtil.checkInter = false
        super.onResume()

    }

    var inForceGround = false

    private var navController: NavController? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        context?.let { ctx ->
            firebaseAnalytics = FirebaseAnalytics.getInstance(ctx)
        }
        navController = findNavController()
    }

    protected fun gotoFrag(currentId: Int, action: Int) {
        try {
            if (navController?.currentDestination!!.id == currentId) {
                navController?.navigate(action)
            }
        } catch (e: Exception) {
        }

    }

    protected fun gotoFrag(currentId: Int, action: Int, bundle: Bundle) {
        try {
            if (navController?.currentDestination!!.id == currentId) {
                navController?.navigate(action, bundle)
            }
        } catch (e: Exception) {
        }

    }

    protected fun onBackToHome(currentId: Int) {
        try {
            if (navController?.currentDestination!!.id == currentId) {
                navController?.popBackStack(R.id.mainFrag, false)
            }
        } catch (e: Exception) {
        }

    }

    fun onBackPress(currentId: Int) {
        try {
            if (navController?.currentDestination!!.id == currentId) {
                navController?.popBackStack()
            }
        } catch (e: Exception) {
        }

    }

    fun isNetworkConnected(): Boolean {
        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun showAdsInter(
        spaceName: String,
        timeout: Long,
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        if (AppUtil.isIAP || !isNetworkConnected()) {
            Log.e("TAGG", "onSuccess: ")
            onSuccess()
        } else {

            AppUtil.checkInter = true
            AdsController.getInstance().loadAndShow(
                spaceName = spaceName,
                timeMillisecond = timeout,
                loadingText = "Loading Ads",
                adCallback = object : AdCallback {
                    override fun onAdShow(network: String, adtype: String) {
                        AppUtil.checkInter = true
                        Log.e("TAGG", "onAdShow: ")
                        onSuccess()
                    }

                    override fun onAdClose(adType: String) {
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        Log.e("TAGG", "onAdFailToLoad: ")
                        AppUtil.checkInter = true
                        onFail()
                    }

                    override fun onAdOff() {

                    }

                    override fun onAdClick() {
                        super.onAdClick()

                    }

                }
            )

        }

    }

    fun showAdsWithLayout(
        spaceName: String,
        viewGroup: ViewGroup
    ) {
        if (AppUtil.isIAP || !isNetworkConnected()) {
            viewGroup.gone()
        } else {
            AdsController.getInstance().loadAndShow(
                spaceName = spaceName,
                layout = viewGroup,
                adCallback = object : AdCallback {
                    override fun onAdShow(network: String, adtype: String) {
                    }

                    override fun onAdClose(adType: String) {
                    }

                    override fun onAdFailToLoad(messageError: String?) {
                        viewGroup.visibility = View.GONE
                    }

                    override fun onAdOff() {

                    }

                    override fun onAdClick() {
                        super.onAdClick()

                    }

                }
            )

        }

    }

    protected fun onBackFinish() {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            activity?.finish()
        }
    }

    var firebaseAnalytics: FirebaseAnalytics? = null

    open fun logEvent(event: String) {
        Log.e("TAG", "logEvent: $event")
        try {
            firebaseAnalytics?.logEvent(event.trim(), Bundle())
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "logEventException: $event: ${e.message}")
        }
    }

    open fun logScreen(event: String) {
        Log.e("TAG", "logScreen: $event")
        try {
            firebaseAnalytics?.setCurrentScreen(
                requireActivity(),
                event.replace(" ", "_"),
                event
            )
        } catch (e: java.lang.Exception) {
        }
    }

    open fun logEvent(event: String, key: String, value: String) {
        try {
            val bundle = Bundle()
            bundle.putString(key.replace(" ", "_"), value.replace(" ", "_"))
            firebaseAnalytics?.logEvent(event.replace(" ", "_"), bundle)
        } catch (e: java.lang.Exception) {
        }
    }

}