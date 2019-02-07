package com.joelzhu.floatwindow;

import android.util.Log;

/**
 * Util class.
 */
public final class FloatWindowHelper {
    public static final String TAG = "FloatWindow";

    public static final int DEFAULT_WINDOW_X = -1;
    public static final int DEFAULT_WINDOW_Y = -1;

    public static void printErrorLog(String logString) {
        Log.e(TAG, logString);
    }

    public static void printInfoLog(String logString) {
        Log.i(TAG, logString);
    }
}