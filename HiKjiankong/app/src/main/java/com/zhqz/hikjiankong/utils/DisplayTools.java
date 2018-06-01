package com.zhqz.hikjiankong.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zhqz.hikjiankong.MvpApplication;
import com.zhqz.hikjiankong.data.db.SharePreferenceUtil;


/**
 * Created by xuezhuguo on 16-7-12.
 */
public class DisplayTools {

    /**
     * @return 包
     * @throws Exception
     */
    public static String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = MvpApplication.context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MvpApplication.context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }
    public static int getVersionCode() {
        // 获取packagemanager的实例
        PackageManager packageManager = MvpApplication.context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MvpApplication.context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = packInfo.versionCode;
        return versionCode;
    }

    /**
     * @return uuid
     */
    public static String getUdid(SharePreferenceUtil prefs) {
        if (prefs.getUdid() == null) {
            prefs.setUdid(java.util.UUID.randomUUID().toString());
        }
        return prefs.getUdid();
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }



}
