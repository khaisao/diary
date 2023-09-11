package com.sutech.diary.model

data class ContentModel(
    var id: Int = 0,
    var createDate: String = "",
    var updateDate: String = "",
    var title: String = "",
    var content: String = "",
    var images: ArrayList<ImageObj> = ArrayList(),
    var password: String? = null,
    var dateTimeCreate: String = "",
    var dateTimeUpdate: String = ""
)