package com.zhqz.hikjiankong.data;

import com.google.gson.annotations.SerializedName;

public class Datas<T> {

    @SerializedName("datas")
    T datas;

    public T getDatas() {
        return datas;
    }

    @Override
    public String toString() {
        return "Datas{" +
                "datas=" + datas +
                '}';
    }
}
