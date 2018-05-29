package com.zhqz.faces.data.model;

import android.graphics.Rect;

import java.util.Arrays;

public class FaceFeature {
    /** 图片路径 */
    /**
     * the path of image
     */
    public String mImagePath = null;
    /** 图片缩略图路径 */
    /**
     * the path of thumbnail
     */
    public String mThumbnailPath = null;
    /** 特征索引 */
    /**
     * the index of the feature
     */
    public int mFeatureIndex = 0;
    /** 字节数组格式的特征 */
    /**
     * the byte array feature
     */
    public byte[] mByteFeature = null;
    /** 人脸框字符串 */
    /**
     * the string of face rectangle
     */
    public String mFaceRect = null;
    public String mFeature = null;

    public FaceFeature(String imagePath, String thumbnailPath, String feature, Rect facerect) {
        this.mImagePath = imagePath;
        this.mFeature = feature;
        this.mThumbnailPath = thumbnailPath;
        Rect rect = new Rect(facerect.left, facerect.top, facerect.right, facerect.bottom);
        this.mFaceRect = rect.flattenToString();
    }

    @Override
    public String toString() {
        return "FaceFeature{" +
                "mImagePath='" + mImagePath + '\'' +
                ", mThumbnailPath='" + mThumbnailPath + '\'' +
                ", mFeatureIndex=" + mFeatureIndex +
                ", mByteFeature=" + Arrays.toString(mByteFeature) +
                ", mFaceRect='" + mFaceRect + '\'' +
                ", mFeature='" + mFeature + '\'' +
                '}';
    }
}
