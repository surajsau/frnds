package com.halfplatepoha.frnds;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class FrndsPreference {

    private static SharedPreferences sharedpreferences;


    public static SharedPreferences init(Context appCtx, String file) {
        sharedpreferences = appCtx.getSharedPreferences(file,
                Context.MODE_PRIVATE);

        return sharedpreferences;
    }

    public static void setInPref(SharedPreferences sharedpreferences, String key, String value) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setInPref(String key, boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static void setInPref(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setInPref(SharedPreferences sharedpreferences, String key, long value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void setIntegerInPref(SharedPreferences sharedpreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setInPref(String key, float value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void setInPref(String key, int value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getFromPref(SharedPreferences sharedpreferences, String key, String defaultValue) {
        return sharedpreferences.getString(key, defaultValue);
    }

    public static void setBooleanInPref(SharedPreferences sharedPreferences, String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getFromPref(String key, String defaultValue) {
        return sharedpreferences.getString(key, defaultValue);
    }

    public static boolean getBooleanFromPref(String key, Boolean defaultValue) {
        return sharedpreferences.getBoolean(key, defaultValue);
    }

    public static long getLongFromPref(String key, long defaultValue) {
        return sharedpreferences.getLong(key, defaultValue);
    }

    public static int getIntFromPref(String key, int defaultValue) {
        return sharedpreferences.getInt(key, defaultValue);
    }

    public static void removeFromPref(String key) {
        sharedpreferences.edit().remove(key).apply();
    }

}
