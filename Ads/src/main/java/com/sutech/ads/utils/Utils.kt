package com.sutech.ads.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.io.InputStream

object Utils {
    fun showToastDebug(context: Context?,text:String){
        if(context != null && Constant.isShowToastDebug) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }
    }
    fun getStringAssetFile(path: String,activity:Activity): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = activity.assets.open(path)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}