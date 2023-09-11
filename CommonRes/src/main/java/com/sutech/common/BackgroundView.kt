package com.sutech.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View


@SuppressLint("AppCompatCustomView","CustomViewStyleable")
class BackgroundView : View {
    constructor(context: Context?) : super(context) {
        this.setBackgroundColor(ColorUtil.instant!!.colorBackground)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.setBackgroundColor(ColorUtil.instant!!.colorBackground)
    }
    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        this.setBackgroundColor(ColorUtil.instant!!.colorBackground)
    }

}