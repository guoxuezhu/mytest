package com.zhqz.mvp.data.remote;

import com.google.gson.annotations.SerializedName;

public class JsonRequest {
    static public class Factory {
        static public LoginRequest loginReq(String userName, String password, String deviceId) {
            return new LoginRequest(userName, password, deviceId);
        }
    }

    static public class LoginRequest {
        @SerializedName("userName")
        String userName;
        @SerializedName("password")
        String password;
        @SerializedName("deviceId")
        String deviceId;

        LoginRequest(String u, String p, String d) {
            userName = u;
            password = p;
            deviceId = d;
        }
    }
}
