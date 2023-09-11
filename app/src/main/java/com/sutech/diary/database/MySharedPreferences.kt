package com.sutech.diary.database

import android.content.Context

private const val TAG = "MySharedPreferences"
class MySharedPreferences {
    private var mContext: Context? = null

    private constructor() {}
    constructor(mContext: Context?) {
        this.mContext = mContext
    }

    /**
     * Save a long integer to MySharedPreferences
     *
     * @param key
     * @param n
     */
    fun putLongValue(key: String?, n: Long) {
        // SmartLog.log(TAG, "Set long integer value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        val editor = pref.edit()
        editor.putLong(key, n)
        editor.commit()
    }

    /**
     * Read a long integer to MySharedPreferences
     *
     * @param key
     * @return
     */
    fun getLongValue(key: String?): Long {
        // SmartLog.log(TAG, "Get long integer value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getLong(key, 0)
    }

    /**
     * Save an integer to MySharedPreferences
     *
     * @param key
     * @param n
     */
    fun putIntValue(key: String?, n: Int) {
        // SmartLog.log(TAG, "Set integer value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        val editor = pref.edit()
        editor.putInt(key, n)
        editor.commit()
    }

    /**
     * Read an integer to MySharedPreferences
     *
     * @param key
     * @return
     */
    fun getIntValue(key: String?): Int {
        // SmartLog.log(TAG, "Get integer value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getInt(key, -1)
    }

    /**
     * Save an string to MySharedPreferences
     *
     * @param key
     * @param s
     */
    fun putStringValue(key: String?, s: String?) {
        // SmartLog.log(TAG, "Set string value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        val editor = pref.edit()
        editor.putString(key, s)
        editor.commit()
    }

    /**
     * Read an string to MySharedPreferences
     *
     * @param key
     * @return
     */
    fun getStringValue(key: String?): String? {
        // SmartLog.log(TAG, "Get string value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getString(key, "")
    }

    /**
     * Read an string to MySharedPreferences
     *
     * @param key
     * @param defaultValue
     * @return
     */
    fun getStringValue(key: String?, defaultValue: String?): String? {
        // SmartLog.log(TAG, "Get string value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getString(key, defaultValue)
    }

    /**
     * Save an boolean to MySharedPreferences
     *
     * @param key
     * @params
     */
    fun putBooleanValue(key: String?, b: Boolean?) {
        // SmartLog.log(TAG, "Set boolean value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        val editor = pref.edit()
        editor.putBoolean(key, b!!)
        editor.commit()
    }

    /**
     * Read an boolean to MySharedPreferences
     *
     * @param key
     * @return
     */
    fun getBooleanValue(key: String?,defaultValue: Boolean): Boolean {
        // SmartLog.log(TAG, "Get boolean value");
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getBoolean(key, defaultValue)
    }

    /**
     * Save an float to MySharedPreferences
     *
     * @param key
     * @params
     */
    fun putFloatValue(key: String?, f: Float) {
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        val editor = pref.edit()
        editor.putFloat(key, f)
        editor.commit()
    }

    /**
     * Read an float to MySharedPreferences
     *
     * @param key
     * @return
     */
    fun getFloatValue(key: String?): Float {
        val pref = mContext!!.getSharedPreferences(
            APP_PREFERENCES, 0
        )
        return pref.getFloat(key, 0.0f)
    }

    companion object {
        private const val APP_PREFERENCES = "APP_PREFERENCES"
    }
}