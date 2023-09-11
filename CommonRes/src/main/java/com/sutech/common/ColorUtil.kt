package com.sutech.common
import android.graphics.Color


class ColorUtil {
    var colorText: Int = 0
    var colorEditText: Int = 0
    var colorSelected: Int = 0
    var colorTextSecondary: Int = 0
    var colorBackground: Int = 0
    var colorIcon: Int = 0
    fun initColor(textColor: String? = "#000000",colorSelected: String? = "#febd00", colorEditTex: String? = "#252525", colorTextSecond: String? = "#252525",backgroundColor: String? = "#f7f7f7",iconColor: String? = "#febd00") {
        instance!!.colorText = Color.parseColor(textColor)
        instance!!.colorEditText = Color.parseColor(colorEditTex)
        instance!!.colorSelected = Color.parseColor(colorSelected)
        instance!!.colorTextSecondary = Color.parseColor(colorTextSecond)
        instance!!.colorBackground = Color.parseColor(backgroundColor)
        instance!!.colorIcon = Color.parseColor(iconColor)
    }

    companion object {
        private var instance: ColorUtil? = null
        val instant: ColorUtil?
            get() {
                if (instance == null) {
                    instance = ColorUtil()
                }
                return instance
            }
    }
}