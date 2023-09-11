package com.sutech.common

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.utils.widget.ImageFilterView


class IconView : ImageFilterView {
    constructor(context: Context?) : super(context) {
        this.setColorFilter(ColorUtil.instant!!.colorIcon)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.setColorFilter(ColorUtil.instant!!.colorIcon)
    }

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        this.setColorFilter(ColorUtil.instant!!.colorIcon)
    }
}