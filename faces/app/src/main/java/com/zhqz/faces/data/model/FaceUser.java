package com.zhqz.faces.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guoxuezhu on 18-5-27.
 */
public class FaceUser {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("number")// 学号/教职工号
    public String number;

    @SerializedName("status")// 状态
    public int status;// 0 未录入 1已录入

    @Override
    public String toString() {
        return "FaceUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", status=" + status +
                '}';
    }
}
