package com.zhqz.mvp.data.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * 共享参数类
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
    private SharedPreferences sp;
    private Editor editor;


    /**
     * 构造函数
     */
    public SharePreferenceUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        // 利用edit()方法获取Editor对象。
        editor = sp.edit();
    }

    /**
     * @param udid
     */
    public void setUdid(String udid) {
        editor.putString("udid", udid);
        editor.commit();
    }

    public String getUdid() {
        return sp.getString("udid", null);
    }


    public void setSchoolId(int SchoolId) {
        editor.putInt("SchoolId", SchoolId);
        editor.commit();
    }

    public int getSchoolId() {
        return sp.getInt("SchoolId", -1);
    }

    public void setUserString(String userString) {
        editor.putString("user", userString);
        editor.commit();
    }

    public String getUserString() {
        return sp.getString("user", null);
    }

}
