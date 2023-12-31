package com.sutech.diary.view.password

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.sutech.common.PassCodeView
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.AppUtil
import com.sutech.diary.util.Constant
import com.sutech.diary.util.Constant.COME_FROM_SECURITY
import com.sutech.diary.util.Constant.TYPE_PASSWORD
import com.sutech.diary.util.ImageUtil
import com.sutech.diary.util.show
import com.sutech.diary.view.password.SecurityQuesFrag.Companion.TYPE_INPUT_NEW_SECURITY
import com.sutech.diary.view.password.SecurityQuesFrag.Companion.TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.test.dialognew.setPreventDoubleClick
import kotlinx.android.synthetic.main.fragment_pass_word.btn0
import kotlinx.android.synthetic.main.fragment_pass_word.btn1
import kotlinx.android.synthetic.main.fragment_pass_word.btn2
import kotlinx.android.synthetic.main.fragment_pass_word.btn3
import kotlinx.android.synthetic.main.fragment_pass_word.btn4
import kotlinx.android.synthetic.main.fragment_pass_word.btn5
import kotlinx.android.synthetic.main.fragment_pass_word.btn6
import kotlinx.android.synthetic.main.fragment_pass_word.btn7
import kotlinx.android.synthetic.main.fragment_pass_word.btn8
import kotlinx.android.synthetic.main.fragment_pass_word.btn9
import kotlinx.android.synthetic.main.fragment_pass_word.btnDelete
import kotlinx.android.synthetic.main.fragment_pass_word.btnReset
import kotlinx.android.synthetic.main.fragment_pass_word.iv_theme
import kotlinx.android.synthetic.main.fragment_pass_word.layoutAdsPassword
import kotlinx.android.synthetic.main.fragment_pass_word.ll_forgot_password
import kotlinx.android.synthetic.main.fragment_pass_word.passCodeView
import kotlinx.android.synthetic.main.fragment_pass_word.tvCancel
import kotlinx.android.synthetic.main.fragment_pass_word.tvPasscode
import kotlinx.android.synthetic.main.fragment_pass_word.tvWrongPass
import kotlinx.android.synthetic.main.fragment_pass_word.tv_loading_ad


class PassWordFrag : BaseFragment(R.layout.fragment_pass_word) {

    var passwordBefore = ""
    var passwordAfter = ""
    var oldPassword = ""

    /**
     * $param 0: create`
     * $param 1: update
     * $param 2: check
     * $param 3: check splash
     */
    var isTypePassword = -1
    private var needLoadAds = false

    private var adView: AdView? = null


    override fun initView() {
        getDataBundle()
        ImageUtil.setThemeForImageView(iv_theme, requireContext())

        if (isTypePassword == -1) {
            if (DataStore.getPassword().isNullOrBlank()) {
                gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
            } else {
                isTypePassword = 2
            }
        } else if (isTypePassword == 1) {
            tvPasscode.text = getString(R.string.enter_old_password)
            logEvent("EnterCuPassword_Show")

        } else if (isTypePassword == 0) {
            logEvent("SetpassScr_Show")
            tvPasscode.text = getString(R.string.set_password)
        }
        if (isTypePassword != 3) {
            ll_forgot_password.isVisible = false
            Glide.with(requireContext()).load(R.drawable.ic_back).into(tvCancel)
        } else {
            layoutAdsPassword.visibility = View.VISIBLE
            Glide.with(requireContext()).load(R.drawable.ic_cancel).into(tvCancel)
        }
        if (isTypePassword == 2) {
            layoutAdsPassword.isVisible = true
            ll_forgot_password.isVisible = true
        }
        needLoadAds = isTypePassword == 2 || isTypePassword == 3
        if (AppUtil.isIAP || !isNetworkConnected()) {
            needLoadAds = false
        }
        layoutAdsPassword.isVisible = needLoadAds
        setClick()
        MobileAds.initialize(requireContext(),
            { })
        try {
            layoutAdsPassword.post {
                val adView = AdView(requireContext())
                adView.adUnitId = BuildConfig.ID_AD_INLINES_BANNER
                adView.setAdSize(
                    getAdSize(
                        requireContext(),
                        layoutAdsPassword.width,
                        layoutAdsPassword.height
                    )
                )
                adView.loadState { isLoaded ->
                    if (isLoaded) {
                        tv_loading_ad.isVisible = false
                    }
                }
                val adRequest = AdRequest.Builder().build()
                layoutAdsPassword.removeAllViews()
                layoutAdsPassword.addView(adView)
                if (needLoadAds) {

                    adView.loadAd(adRequest)
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun getAdSize(context: Context, width: Int, height: Int): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val density: Float = displayMetrics.density
        val adWidth: Int = (width / density).toInt()
        val adHeight: Int = (height / density).toInt()
        return AdSize.getInlineAdaptiveBannerAdSize(adWidth, adHeight)
    }

    private fun AdView.loadState(callback: ((Boolean) -> Unit)? = null) {
        adListener = object : AdListener() {
            override fun onAdLoaded() {
                callback?.invoke(true)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                callback?.invoke(false)
            }
        }
    }

    private fun getAdSize(height: Int): AdSize {
        val display: Display = requireActivity().windowManager.getDefaultDisplay()
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        val adHeight = (height / density).toInt()
        return AdSize.getInlineAdaptiveBannerAdSize(adWidth, adHeight)
    }

    private fun getDataBundle() {
        val bundle = arguments
        if (bundle != null) {
            val type = bundle.getInt(TYPE_PASSWORD, -1)
            val comeFromSecurity = bundle.getBoolean(COME_FROM_SECURITY, false)
            if (type != -1) {
                isTypePassword = if (type == 3) {
                    3
                } else {
                    if (DataStore.getPassword().isNullOrEmpty() || comeFromSecurity) {
                        0
                    } else {
                        1
                    }
                }
            }
        }
        if (isTypePassword == 3) {
            logEvent("UnlockPass_Show")
        }
    }

    private fun setClick() {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (isTypePassword == 2 || isTypePassword == 3) {
                activity?.finish()
            } else {
                onBackPress(R.id.passWordFrag)
            }
        }
        btn1.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(1)
        }
        btn2.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(2)
        }
        btn3.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(3)
        }
        btn4.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(4)
        }
        btn5.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(5)
        }
        btn6.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(6)
        }
        btn7.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(7)
        }
        btn8.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(8)
        }
        btn9.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(9)
        }
        btn0.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(0)
        }

        tvCancel.setOnClickListener {
            if (tvPasscode.text.toString() == getString(R.string.enter_new_password)) {
                logEvent("EnterNewPassword_IconBack_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.enter_old_password)) {
                logEvent("OldPassword_Back_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.set_password)
            ) {
                logEvent("SetpassScr_IconBack_Clicked")
                DataStore.setUsePassword(false)
            } else if (tvPasscode.text.toString() == getString(R.string.confirm_password)
            ) {
                DataStore.setUsePassword(false)
                logEvent("ConfirmPassword_IconBack_Clicked")
            } else if (tvPasscode.text.toString() == getString(
                    R.string.confirm_new_password
                )
            ) {
                logEvent("ConfirmNewScr_IconBack_Clicked")
            } else if (tvPasscode.text.toString() == getString(
                    R.string.enter_pass_word
                )
            ) {
                logEvent("UnlockPass_IconBack_Clicked")
            } else {
                logEvent("SetpassScr_IconSkip_Clicked")
            }
            if (isTypePassword == 2 || isTypePassword == 3) {
                activity?.finish()
            } else {
                onBackPress(R.id.passWordFrag)
            }
        }
        btnReset.setPreventDoubleClick(300) {
            passCodeView.clearPassCode()
        }
        btnDelete.setPreventDoubleClick(300) {
            passCodeView.removePassCode()
        }

        ll_forgot_password.setOnClickListener {
            goToSecurityScreen()
        }

        passCodeView.setOnDoneListener(object : PassCodeView.PassCodeViewListener {
            override fun onPassCodeDone(passCode: String) {
                when (isTypePassword) {
                    0 -> {
//                        create
                        if (passwordBefore.isEmpty()) {
                            passwordBefore = passCode
                            passCodeView.clearPassCode()
                            tvPasscode.text = getString(R.string.confirm_password)
                            logEvent("ConfirmPassword_Show")
                        } else {
                            passwordAfter = passCode
                            if (passwordAfter == passwordBefore) {
                                DataStore.savePassword(passCode)
                                DataStore.setUsePassword(true)
                                if (!DataStore.getAns().isNullOrEmpty()
                                ) {
                                    gotoFrag(
                                        R.id.passWordFrag,
                                        R.id.action_passWordFrag_to_mainFrag
                                    )
                                } else {
                                    goToSecurityAndClearPasswordScreenInBackStack()
                                }
                            } else {
                                tvWrongPass.text = getString(R.string.re_password_wrong)
                                passCodeWrong()
                            }
                        }
                    }

                    1 -> {
//                        update
                        if (oldPassword.isEmpty()) {
                            if (DataStore.checkPassword(passCode)) {
                                oldPassword = passCode
                                tvPasscode.text = getString(R.string.enter_new_password)
                                logEvent("EnterNewPassword_Show")
                            } else {
                                tvWrongPass.text = getString(R.string.old_password_wrong)
                                passCodeWrong()
                            }
                            passCodeView.clearPassCode()
                        } else if (passwordBefore.isEmpty()) {
                            passwordBefore = passCode
                            passCodeView.clearPassCode()
                            tvPasscode.text = getString(R.string.confirm_new_password)
                            logEvent("ConfirmNewScr_Show")
                        } else {
                            passwordAfter = passCode
                            passCodeView.clearPassCode()
                            if (passwordAfter == passwordBefore) {
                                DataStore.savePassword(passCode)
                                DataStore.setUsePassword(true)
                                onBackPress(R.id.passWordFrag)
                            } else {
                                tvWrongPass.text = getString(R.string.re_password_wrong)
                                passCodeWrong()
                            }
                        }
                    }

                    2 -> {
//                        check
                        if (DataStore.checkPassword(passCode)) {
                            findNavController().popBackStack()
//                            gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
                        } else {
                            tvWrongPass.text = getString(R.string.wrong_password)
                            passCodeWrong()
                        }

                    }

                    3 -> {
//                        check splash
                        if (DataStore.checkPassword(passCode)) {
//                            findNavController().popBackStack()
                            gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
                        } else {
                            tvWrongPass.text = getString(R.string.wrong_password)
                            passCodeWrong()
                        }

                    }
                }
            }

        })
    }

    private fun goToSecurityScreen() {
        logEvent("UnlockPass_Forgotpassword")
        val ans = DataStore.getAns()
        val bundle = Bundle()
        if (ans.isNullOrBlank()) {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_NEW_SECURITY)
        } else {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD)
        }
        bundle.putBoolean(Constant.GO_TO_SECURITY_FROM_CHANGE_PASSWORD, true)
        gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_securityQuesFrag, bundle)
    }

    private fun goToSecurityAndClearPasswordScreenInBackStack() {
        val ans = DataStore.getAns()
        val bundle = Bundle()
        if (ans.isNullOrBlank()) {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_NEW_SECURITY)
        } else {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD)
        }
        bundle.putBoolean(Constant.GO_TO_SECURITY_FROM_CHANGE_PASSWORD, true)
        gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_securityQuesFrag2, bundle)
    }

    private fun passCodeWrong() {
        tvWrongPass.show()
        passCodeView.clearPassCode()
        passCodeView.playWrongPassAnimation()
    }
}