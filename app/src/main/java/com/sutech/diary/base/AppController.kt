package com.sutech.diary.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.sutech.diary.database.DataStore
import com.sutech.common.ColorUtil
import com.sutech.common.TypeFaceUtil
import com.sutech.journal.diary.diarywriting.lockdiary.R


class AppController : Application() {
    // true if user is updated
    var oldThemeId = 0
    var themeId = 0

    private object Holder {
        val INSTANCE = AppController()
    }

    override fun onCreate() {
        super.onCreate()
        DataStore.init(this)
        MobileAds.initialize(this)
        AudienceNetworkAds.initialize(this)
        FacebookSdk.sdkInitialize(applicationContext)
        TypeFaceUtil.instant?.initTypeFace(this)
        if (DataStore.getTheme() != -1) {
            setThemeApp(DataStore.getTheme(), applicationContext)
        } else {
            setThemeApp(0,applicationContext)
        }
    }

    fun setThemeApp(position: Int, context: Context) {
        Log.e("TAG", "setThemeApp: $position")
        if (position != -1 && position != -2) {
            themeId = position
        } else if (position == -2) {
            themeId = oldThemeId
        }
        Log.e("TAG", "setThemeApp:themeId $themeId")
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit()
            .putInt("ActivityTheme", when (themeId) {
                0 -> R.style.Default
                1 -> R.style.Theme1
                2 -> R.style.Theme2
                3 -> R.style.Theme3
                4 -> R.style.Theme4
                5 -> R.style.Theme5
                6 -> R.style.Theme6
                7 -> R.style.Theme7
                else -> R.style.Default
            })
            .apply();
    }

    fun setTextColor(
        textColor: String? = "#000000",
        colorSelected: String? = "#000000",
        colorEditTex: String? = "#252525",
        colorTextSecond: String? = "#000000",
        backgroundColor: String? = "#F2F2F2",
        iconColor: String? = "#252525"
    ) {
        ColorUtil.instant!!.initColor(
            textColor,
            colorSelected,
            colorEditTex,
            colorTextSecond,
            backgroundColor,
            iconColor
        )

    }

    companion object {
        @JvmStatic
        fun getInstance(): AppController {
            return Holder.INSTANCE
        }
    }
}