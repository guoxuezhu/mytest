package com.zhqz.hikjiankong.utils;

import android.util.Log;

import com.zhqz.hikjiankong.BuildConfig;


/**
 * 打印log日志
 */
public class ELog {
    public static boolean isDebug = BuildConfig.JAVA_LOG_ENABLED;
    public static void i(Object msg) {
        if (isDebug){
            Log.i("zhqz", "" +msg);
        }
    }

    public static void d(Object msg) {
        if (isDebug){
            Log.d("zhqz", "" +msg);
        }
    }

    public static void e(Object msg) {
        if (isDebug){
            Log.e("zhqz", "" +msg);
        }
    }
}