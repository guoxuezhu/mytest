package com.zhqz.hikjiankong.data;

import com.google.gson.annotations.SerializedName;

public class HttpResult<T> {
    @SerializedName("success")
    public boolean result;
    @SerializedName("message")
    public String msg;
    @SerializedName("code")
    public String code;

    @SerializedName("data")
    T data;

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "result=" + result +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
