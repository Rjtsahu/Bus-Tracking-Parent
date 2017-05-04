package com.sahurjt.btsparent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    private static final String SP_FILE = "pref";
    public static final String DEFAULT_STRING = "";
    private static final int DEFAULT_INT = 0;

    private static SharedPrefHelper this_context=null;
    private static SharedPreferences sharedPreferences=null;

    // prevent constructor construction
    private SharedPrefHelper(final Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);

    }

    public static SharedPrefHelper getInstance(Context ctx) {

        if (this_context == null) {
            this_context = new SharedPrefHelper(ctx);
        }
        return this_context;
    }

    public void addString(String key, String value) {
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString(key, value);
        spEditor.apply();
    }

    public void addBoolean(String key, Boolean value) {
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putBoolean(key, value);
        spEditor.apply();
    }

    public void addInteger(String key, Integer value) {
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putInt(key, value);
        spEditor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, DEFAULT_STRING);
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, DEFAULT_INT);
    }

    // constant related to key
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    public static final String LOGIN_TOKEN = "token";
    public static final String LOGIN_TOKEN_VALIDITY = "valid_till";
    public static final String PARENT_NAME = "parent_name";
    public static final String PARENT_EMAIL = "parent_email";
    public static final String PARENT_PHONE = "parent_phone";
   // public static final String SCHOOL_GPS="gps_school";
    public static final String HOME_GPS="gps_home";
}
