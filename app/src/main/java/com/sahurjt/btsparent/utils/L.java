package com.sahurjt.btsparent.utils;

import android.content.ContentProviderOperation;
import android.util.Log;

/**
 * Created by Rajat_Sahu on 03-04-2017.
 */
// a utility class to easily write logs
public class L {
    private static final String OK = "bts_ok";
    private static final String ERROR = "bts_error";
    private static final String VERBOSE = "bts_testing";

    public static void err(String str) {
        Log.e(ERROR, str);
    }

    public static void err(String tag, String str) {
        Log.e(ERROR, tag + ":" + str);
    }

    public static void ok(String tag, String str) {
        Log.d(OK, tag + ":" + str);
    }

    public static void ok(String str) {
        Log.d(OK, str);
    }

    public static void verbose(String str) {
        Log.v(VERBOSE, str);
    }

    public static void verbose(String tag, String str) {
        Log.v(VERBOSE, tag + ":" + str);
    }

}
