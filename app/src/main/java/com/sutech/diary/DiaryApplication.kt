package com.sutech.diary

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds

class DiaryApplication : Application(){
    companion object{
        var interCount = 0;
    }
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AudienceNetworkAds.initialize(this)
        FacebookSdk.sdkInitialize(applicationContext)
    }
}