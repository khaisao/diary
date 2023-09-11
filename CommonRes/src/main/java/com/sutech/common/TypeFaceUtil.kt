package com.sutech.common

import android.content.Context
import android.graphics.Typeface

class TypeFaceUtil {


    var art: Typeface? = null
    var light: Typeface? = null
    var regular: Typeface? = null
    var medium: Typeface? = null
    var semibold: Typeface? = null
    var bold: Typeface? = null

    fun initTypeFace(context: Context) {
        instance!!.art = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_ART)
        instance!!.light = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_LIGHT)
        instance!!.regular = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_REGULAR)
        instance!!.medium =  Typeface.createFromAsset(context.assets, TextFontConfig.FONT_MEDIUM)
        instance!!.semibold = Typeface.createFromAsset(context.assets,TextFontConfig.FONT_SEMI_BOLD)
        instance!!.bold = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_BOLD)
    }

    companion object {
        private var instance: TypeFaceUtil? = null
        val instant: TypeFaceUtil?
            get() {
                if (instance == null) {
                    instance = TypeFaceUtil()
                }
                return instance
            }
    }
}