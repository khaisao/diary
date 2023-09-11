package com.sutech.diary.model

import android.graphics.Bitmap

data class ImageObj(
    var id:String ="",
    var title:String ="",
    var path: String? = null,
    var bitmap: Bitmap? = null,
    var isSelected: Boolean = false
)
