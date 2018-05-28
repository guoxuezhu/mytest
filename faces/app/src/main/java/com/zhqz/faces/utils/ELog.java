package com.zhqz.faces.utils;

import android.util.Log;

import com.zhqz.faces.BuildConfig;


/**
 * 打印log日志
 */
public class ELog {
    public static boolean isDebug = BuildConfig.JAVA_LOG_ENABLED;

    public static void i(Object msg) {
        if (isDebug) {
            Log.i(BuildConfig.APPLICATION_ID, "" + msg);
        }
    }

    public static void d(Object msg) {
        if (isDebug) {
            Log.d(BuildConfig.APPLICATION_ID, "" + msg);
        }
    }

    public static void e(Object msg) {
        if (isDebug) {
            Log.e(BuildConfig.APPLICATION_ID, "" + msg);
        }
    }

}