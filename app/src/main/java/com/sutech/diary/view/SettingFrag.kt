package com.sutech.diary.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.*
import com.sutech.diary.util.AppUtil.openBrowser
import com.sutech.diary.util.AppUtil.openMarket
import com.sutech.diary.util.AppUtil.sendEmailMore
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFrag : BaseFragment(R.layout.fragment_setting) {
    override fun initView() {
//        context?.let { ctx ->
//            AdsUtil.loadInterstitial(ctx, getString(R.string.interstitial_setting))
//        }
        logEvent("SettingScr_Show")
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            backSetting()
        }
        initOnClick()
    }

    override fun onResume() {
        super.onResume()
        swPassword?.isChecked = DataStore.getUsePassword()
        setSwDarkTheme()
    }

    private fun initOnClick() {

        btnSettingBack?.setOnClick(1500) {
            backSetting()
        }
        btnLock?.setOnClick(1500) {
            logEvent("SettingScr_Changepasscode_Clicked")
            val bundle = Bundle()
            bundle.putInt(Constant.TYPE_PASSWORD, 1)
            gotoFrag(R.id.settingFrag, R.id.action_settingFrag_to_passWordFrag, bundle)
        }
        btnSettingFeedBack?.setOnClick(1500) {
            context?.let { ctx ->
                logEvent("SettingScr_Feedback_Clicked")
                sendEmailMore(
                    ctx,
                    arrayOf("Sutechmobile@gmail.com"),
                    "Feedback to My diary ${BuildConfig.VERSION_NAME}",
                    ""
                )
            }
        }
        btnSettingRateApp?.setOnClick(1500) {
            context?.let { ctx ->
                openMarket(ctx, requireActivity().packageName)
            }
        }
        btnSettingPolicy?.setOnClick(1500) {
            logEvent("SettingScr_Policy_Clicked")
            openBrowser(requireContext(),"https://sutechmobile.blogspot.com/2022/12/my-diary-privacy-policy.html")
        }
        btnUsePassword?.setOnClick(500) {
            if (!DataStore.getUsePassword()) {
                if (DataStore.getPassword().isNullOrBlank()) {
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 1)
                    gotoFrag(R.id.settingFrag, R.id.action_settingFrag_to_passWordFrag, bundle)
                } else {
                    DataStore.setUsePassword(true)
                }
            } else {
                DataStore.setUsePassword(false)
            }
            swPassword?.isChecked = DataStore.getUsePassword()
        }
        btnDarkTheme?.setOnClick(100) {
            logEvent("SettingScr_Changetheme_Clicked")
            gotoFrag(R.id.settingFrag,R.id.action_settingFrag_to_themeFrag)
//            if (AppController.getInstance().themeId == 0) {
//                DataStore.saveTheme(1)
//                AppController.getInstance().setThemeApp(1)
//            } else {
//                AppController.getInstance().setThemeApp(0)
//                DataStore.saveTheme(0)
//            }
//            setSwDarkTheme()
        }

        btnSettingShare?.setOnClick(2000) {
            logEvent("SettingScr_Share_Clicked")
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My diary")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {

            }
        }

    }

    private fun backSetting() {
                onBackPress(R.id.settingFrag)
    }



    private fun setSwDarkTheme() {
        swDarkTheme?.isChecked = DataStore.getTheme() == 1
    }


}