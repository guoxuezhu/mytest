package com.zhqz.mvp.utils;

import android.util.Log;

import com.zhqz.mvp.BuildConfig;


/**
 * 打印log日志
 */
public class ELog {
    public static boolean isDebug = BuildConfig.JAVA_LOG_ENABLED;
    public static void i(Object msg) {
        if (isDebug){
            Log.i("sewage", "" +msg);
        }
    }

}