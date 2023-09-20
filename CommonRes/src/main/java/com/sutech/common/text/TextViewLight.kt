package com.sutech.common.text

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.sutech.common.R
import com.sutech.common.ColorUtil
import com.sutech.common.TypeFaceUtil


@SuppressLint("AppCompatCustomView","CustomViewStyleable")
class TextViewLight : TextView {
    constructor(context: Context?) : super(context) {
        this.typeface = TypeFaceUtil.instant!!.light
        this.setTextColor(ColorUtil.instant!!.colorText)
        this.setTextColor(ColorUtil.instant!!.colorText)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.typeface = TypeFaceUtil.instant!!.light
        initAttr(attrs)
    }

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        this.typeface = TypeFaceUtil.instant!!.light
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.text_style,
            0,
            0
        )
        val textColorDefault = ta.getBoolean(R.styleable.text_style_textColorDefault, false)
        if (textColorDefault) {
            this.setTextColor(ColorUtil.instant!!.colorText)
        }

        ta.recycle()
    }
}