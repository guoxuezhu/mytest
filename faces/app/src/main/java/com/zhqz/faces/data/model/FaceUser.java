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

    @SerializedName("faceInfo")
    public String faceInfo;

    @SerializedName("mFaceRect")//人脸框字符串
    public String mFaceRect;

    @SerializedName("mFeature")//字节数组格式的特征
    public String mFeature;

    @Override
    public String toString() {
        return "FaceUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", faceInfo='" + faceInfo + '\'' +
                ", mFaceRect='" + mFaceRect + '\'' +
                ", mFeature='" + mFeature + '\'' +
                '}';
    }
}
