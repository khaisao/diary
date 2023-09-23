package com.sutech.diary.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.io.File
import java.time.YearMonth
import java.util.*
import java.util.regex.Pattern

object AppUtil {

    var isIAP = false
    var checkInter = false
    const val SKU_FOREVER_FAKE = "remove_ads_fake"
    const val SKU_FOREVER = "remove_ads"
    var PRICE_FOREVER = "2$"
    var PRICE_FOREVER_FAKE = "10$"

    var needUpdateDiary = true
    var readiary: Int = 0
    private var mLastClickTime: Long = 0
    fun getWidthScreen(context: Activity): Int {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getBackgroundItem(context: Context): IntArray {
        return context.resources.getIntArray(R.array.array_background)
    }

    fun getHeightScreen(context: Activity): Int {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    val currentTime: String
        get() = Calendar.getInstance().time.toString() + ""

    private fun clickOneSecond(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
            mLastClickTime = SystemClock.elapsedRealtime()
            return true
        }
        return false
    }

    fun showToast(context: Context?, message: String?) {
        if (clickOneSecond()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun logE(TAG: String?, message: String?) {
        Log.e(TAG, message!!)
    }

    fun showToast(context: Context?, message: Int) {
        try {

            if (clickOneSecond()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {

        }
    }

    fun numberCart(count: Int): String {
        return if (count < 99) {
            count.toString() + ""
        } else {
            99.toString() + "+"
        }
    }

    fun isStrValid(text: String?): Boolean {
        return text != null && !text.isEmpty()
    }

    fun isPhoneValid(text: String?): Boolean {
        return text != null && !text.isEmpty() && text.length >= 9 && text.length <= 11
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern =
            Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isPassWordValid(password: String): Boolean {
        return password.length > 5
    }

    fun confirmPassWordValid(password: String, rePassword: String): Boolean {
        return password == rePassword.trim { it <= ' ' }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val conMgr =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val i = conMgr.activeNetworkInfo ?: return false
        if (!i.isConnected) {
            return false
        }
        return i.isAvailable
    }

//    fun coppyText(context: Context, text: String?) {
//        val cm =
//            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        cm.text = text
//        showToast(context, R.string.copy_successful)
//    }

    fun countDayInMonth(month: Int, year: Int): Int {
        var daysInMonth: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val yearMonthObject: YearMonth = YearMonth.of(year, month)
            daysInMonth = yearMonthObject.lengthOfMonth()
        } else {
            // Create a calendar object and set year and month
            val mycal: Calendar = GregorianCalendar(year, month, 1)
            // Get the number of days in that month
            daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        return daysInMonth
    }

    fun deleteFileFromInternalStorage(imagePath: String): Boolean {
        val file = File(imagePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    fun sendEmailMore(context: Context, addresses: Array<String>, subject: String, body: String) {
        disableExposure()

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")

        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(
            Intent.EXTRA_TEXT, body + "\n\n\n" +
                    "DEVICE INFORMATION (Device information is useful for application improvement and development)"
                    + "\n\n" + getDeviceInfo()
        )

        try {
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email app installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getDeviceInfo(): String {
        val densityText = when (Resources.getSystem().displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "HDPI"
        }

        //TODO: Update android Q
        var megAvailable = 0L
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val bytesAvailable: Long
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
                megAvailable = bytesAvailable / (1024 * 1024)
            }
        }


        return "Manufacturer ${Build.MANUFACTURER}, Model ${Build.MODEL}," +
                " ${Locale.getDefault()}, " +
                "osVer ${Build.VERSION.RELEASE}, Screen ${Resources.getSystem().displayMetrics.widthPixels}x${Resources.getSystem().displayMetrics.heightPixels}, " +
                "$densityText, Free space ${megAvailable}MB, TimeZone ${
                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
                }"
    }

    fun openMarket(context: Context, packageName: String) {
        val i = Intent(Intent.ACTION_VIEW)
        try {
            i.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(i)
        } catch (ex: ActivityNotFoundException) {
            openBrowser(
                context,
                "https://play.google.com/store/apps/details?id=\"" + packageName
            )
        }
    }

    fun openBrowser(context: Context, url: String) {
        var url = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(browserIntent)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}