package com.zhqz.faces.data.model;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Arrays;

/**
 * Created by guoxuezhu on 16-11-15.
 */
@Entity
public class FacesData {

    @Id
    public long id;

    public String name;

    public String sex;

    /** 图片路径 */
    public String mImagePath;

    /** 人脸框字符串 */
    public String mFaceRect;
    public String mFeature;


    @Generated(hash = 118088774)
    public FacesData(long id, String name, String sex, String mImagePath,
            String mFaceRect, String mFeature) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.mImagePath = mImagePath;
        this.mFaceRect = mFaceRect;
        this.mFeature = mFeature;
    }


    @Generated(hash = 1609001979)
    public FacesData() {
    }


    @Override
    public String toString() {
        return "FacesData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", mImagePath='" + mImagePath + '\'' +
                ", mFaceRect='" + mFaceRect + '\'' +
                ", mFeature='" + mFeature + '\'' +
                '}';
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSex() {
        return this.sex;
    }


    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getMImagePath() {
        return this.mImagePath;
    }


    public void setMImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }


    public String getMFaceRect() {
        return this.mFaceRect;
    }


    public void setMFaceRect(String mFaceRect) {
        this.mFaceRect = mFaceRect;
    }


    public String getMFeature() {
        return this.mFeature;
    }


    public void setMFeature(String mFeature) {
        this.mFeature = mFeature;
    }


    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }
}