package com.zhqz.faces.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Arrays;

/**
 * Created by guoxuezhu on 16-11-15.
 */
@Entity
public class FacesData {

    /** 图片路径 */
    /**
     * the path of image
     */
    public String mImagePath;
    /** 图片缩略图路径 */
    /**
     * the path of thumbnail
     */
    public String mThumbnailPath;
    /** 特征索引 */
    /**
     * the index of the feature
     */
    public int mFeatureIndex;
    /** 字节数组格式的特征 */
    /**
     * the byte array feature
     */
    public byte[] mByteFeature;
    /** 人脸框字符串 */
    /**
     * the string of face rectangle
     */
    public String mFaceRect;
    public String mFeature;

    @Generated(hash = 1449755835)
    public FacesData(String mImagePath, String mThumbnailPath, int mFeatureIndex,
            byte[] mByteFeature, String mFaceRect, String mFeature) {
        this.mImagePath = mImagePath;
        this.mThumbnailPath = mThumbnailPath;
        this.mFeatureIndex = mFeatureIndex;
        this.mByteFeature = mByteFeature;
        this.mFaceRect = mFaceRect;
        this.mFeature = mFeature;
    }

    @Generated(hash = 1609001979)
    public FacesData() {
    }

    @Override
    public String toString() {
        return "FacesData{" +
                "mImagePath='" + mImagePath + '\'' +
                ", mThumbnailPath='" + mThumbnailPath + '\'' +
                ", mFeatureIndex=" + mFeatureIndex +
                ", mByteFeature=" + Arrays.toString(mByteFeature) +
                ", mFaceRect='" + mFaceRect + '\'' +
                ", mFeature='" + mFeature + '\'' +
                '}';
    }

    public String getMImagePath() {
        return this.mImagePath;
    }

    public void setMImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getMThumbnailPath() {
        return this.mThumbnailPath;
    }

    public void setMThumbnailPath(String mThumbnailPath) {
        this.mThumbnailPath = mThumbnailPath;
    }

    public int getMFeatureIndex() {
        return this.mFeatureIndex;
    }

    public void setMFeatureIndex(int mFeatureIndex) {
        this.mFeatureIndex = mFeatureIndex;
    }

    public byte[] getMByteFeature() {
        return this.mByteFeature;
    }

    public void setMByteFeature(byte[] mByteFeature) {
        this.mByteFeature = mByteFeature;
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
}
