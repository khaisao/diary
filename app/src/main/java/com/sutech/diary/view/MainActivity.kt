package com.sutech.diary.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.sutech.ads.AdsController
import com.sutech.ads.utils.Constant
import com.sutech.diary.util.Constant.isOpening
import com.sutech.journal.diary.diarywriting.lockdiary.BuildConfig
import com.sutech.journal.diary.diarywriting.lockdiary.R


class MainActivity : AppCompatActivity() {
    var fistOpen = false
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = PreferenceManager.getDefaultSharedPreferences(this).getInt("ActivityTheme", R.style.Default)
        // Set this Activity's theme to the saved theme
        // Set this Activity's theme to the saved theme
        setTheme(theme)
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
        Constant.isShowToastDebug= BuildConfig.DEBUG
        AdsController.init(
            activity = this,
            isDebug = BuildConfig.DEBUG,
            listAppId = arrayListOf(getString(R.string.admob_app_unit_id)),
            packetName = "com.sutech.journal.diary.diarywriting.lockdiary",
            listPathJson = arrayListOf("admob.json"),
            lifecycle = lifecycle
        )
        isOpening =true

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        isOpening = false
    }

}