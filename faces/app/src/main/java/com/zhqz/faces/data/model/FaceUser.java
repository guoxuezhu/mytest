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

    @SerializedName("sex")// 男
    public String sex;

    @SerializedName("faceInfo")// 学号/教职工号
    public String faceInfo;



}
