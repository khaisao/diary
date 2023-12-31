package com.sutech.common.edit_text

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import com.sutech.common.R
import com.sutech.common.ColorUtil
import com.sutech.common.TypeFaceUtil


@SuppressLint("AppCompatCustomView","CustomViewStyleable")
class EditTextSecondary : EditText {
    constructor(context: Context?) : super(context) {
        this.typeface = TypeFaceUtil.instant!!.regular
        this.setTextColor(ColorUtil.instant!!.colorTextSecondary)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.typeface = TypeFaceUtil.instant!!.regular
        initAttr(attrs)
    }

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        this.typeface = TypeFaceUtil.instant!!.regular
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.text_style,
            0,
            0
        )
        val textColorDefault = ta.getBoolean(R.styleable.text_style_textColorDefault, true)
        if (textColorDefault) {
            this.setTextColor(ColorUtil.instant!!.colorEditText)
        }

        ta.recycle()
    }
}