package com.joelzhu.floatwindow;

import android.util.Log;

/**
 * Util class.
 */
public final class FloatWindowHelper {
    public static final String TAG = "FloatWindow";
    
    public static void printErrorLog(String logString) {
        Log.e(TAG, logString);
    }
    
    public static void printInfoLog(String logString) {
        Log.i(TAG, logString);
    }
}