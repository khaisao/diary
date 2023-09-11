package com.sutech.diary.base

import android.app.Application
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.sutech.diary.database.DataStore
import com.sutech.common.ColorUtil
import com.sutech.common.TypeFaceUtil


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
            setThemeApp(DataStore.getTheme())
        } else {
            setThemeApp(0)
        }
    }

    fun setThemeApp(position: Int) {
        Log.e("TAG", "setThemeApp: $position")
        if (position != -1 && position != -2) {
            themeId = position
        } else if (position == -2) {
            themeId = oldThemeId
        }
        Log.e("TAG", "setThemeApp:themeId $themeId")
        when (themeId) {
            0 -> {
//                light theme
                setTextColor()
            }
            1 -> {
//               dark theme
                setTextColor(
                    "#F2F2F2",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#000000",
                    "#F2F2F2"
                )
            }
            2 -> {
                setTextColor(
                    "#F2F2F2",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#304FFE",
                    "#000000"
                )
            }
            3 -> {
                setTextColor(
                    "#F2F2F2",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#D50000",
                    "#febd00"
                )
            }
            4 -> {
                setTextColor(
                    "#F2F2F2",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#00C853",
                    "#febd00"
                )
            }
            5 -> {
                setTextColor(
                    "#F2F2F2",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#AA00FF",
                    "#000000"
                )
            }
            6 -> {
                setTextColor(
                    "#000000",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#FFAB00",
                    "#000000"
                )
            }
            7 -> {
                setTextColor(
                    "#000000",
                    "#febd00",
                    "#F2F2F2",
                    "#163DFF",
                    "#FF6D00",
                    "#000000"
                )
            }
            8 -> {
                setTextColor(
                    "#000000",
                    "#febd00",
                    "#000000",
                    "#163DFF",
                    "#F67FB5",
                    "#F2F2F2"
                )
            }
        }
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