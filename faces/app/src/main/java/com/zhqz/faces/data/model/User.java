package com.zhqz.faces.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qingchengl on 16-1-8.
 */
public class User {

    @SerializedName("professionId")
    public String professionId;
    @SerializedName("name")
    public String name;         /* only exist in use/show iterface */
    @SerializedName("userId")
    public String userId;
    public @SerializedName("userName")
    String userName;
    public @SerializedName("sex")
    String sex;
    @SerializedName("status")
    String status;
    @SerializedName("roleId")
    public String roleId;
    @SerializedName("token")
    public String token;
    @SerializedName("securetId")
    public String securetId;

    @SerializedName("deviceId")
    String deviceId;

    // FIXME: remove this getters
    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getSecuretId() {
        return securetId;
    }

    public boolean isValid() {
        return !(TextUtils.isEmpty(professionId)
                || TextUtils.isEmpty(securetId)
                || TextUtils.isEmpty(userId)
                || TextUtils.isEmpty(userName)
                || TextUtils.isEmpty(roleId)
                || TextUtils.isEmpty(token)
                );
    }

    @Override
    public String toString() {
        return "User{" +
                "professionId='" + professionId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", sex='" + sex + '\'' +
                ", status='" + status + '\'' +
                ", roleId='" + roleId + '\'' +
                ", token='" + token + '\'' +
                ", securetId='" + securetId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
