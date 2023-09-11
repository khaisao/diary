package com.sutech.diary.database;

import android.content.Context;


public class DataStore {

    protected static DataStore instance;
    protected MySharedPreferences sharedPreferences;



    private static final String KEY_PREF_RATED = "KEY_PREF_RATED";
    private static final String KEY_PREF_IS_INSTALLED = "PREF_IS_INSTALLED";
    private static final String KEY_PREF_PASSWORD = "KEY_PREF_PASSWORD";
    private static final String KEY_PREF_CHECK_SKIP_PASSWORD= "KEY_PREF_CHECK_SKIP_PASSWORD";
    private static final String KEY_PREF_USE_PASSWORD = "KEY_PREF_USE_PASSWORD";
    private static final String KEY_PREF_THEME_APP = "KEY_PREF_THEME_APP";
    private static final String KEY_PREF_CONFIRM_PASSWORD = "KEY_PREF_CONFIRM_PASSWORD";

    /**
     * Call when start application
     */
    public static void init(Context context) {
        instance = new DataStore();
        instance.sharedPreferences = new MySharedPreferences(context);
    }

    public static DataStore getInstance() {
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    public static Boolean checkRated() {
        return getInstance().sharedPreferences.getBooleanValue(KEY_PREF_RATED,false);
    }

    public static void setRated(boolean isFirstOpen) {
        getInstance().sharedPreferences.putBooleanValue(KEY_PREF_RATED,isFirstOpen);
    }

    public static void saveTheme(Integer themeId) {
        getInstance().sharedPreferences.putIntValue(KEY_PREF_THEME_APP, themeId);
    }

    public static Integer getTheme() {
        return getInstance().sharedPreferences.getIntValue(KEY_PREF_THEME_APP);
    }


    public static void savePassword(String password) {
        getInstance().sharedPreferences.putStringValue(KEY_PREF_PASSWORD, password);
    }

    public static String getPassword() {
        return getInstance().sharedPreferences.getStringValue(KEY_PREF_PASSWORD);
    }

    public static void setUsePassword(Boolean password) {
        getInstance().sharedPreferences.putBooleanValue(KEY_PREF_USE_PASSWORD, password);
    }

    public static Boolean getUsePassword() {
        return getInstance().sharedPreferences.getBooleanValue(KEY_PREF_USE_PASSWORD,true);
    }


    public static void  setConFirmPassword(Boolean password) {
          getInstance().sharedPreferences.putBooleanValue(KEY_PREF_CONFIRM_PASSWORD,password);
    }
    public static Boolean conFirmPassword() {
        return getInstance().sharedPreferences.getBooleanValue(KEY_PREF_CONFIRM_PASSWORD,false);
    }

    public static Boolean checkPassword(String password) {
        return password.equals(getInstance().sharedPreferences.getStringValue(KEY_PREF_PASSWORD));
    }


}
