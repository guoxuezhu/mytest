package com.zhqz.faces.data.model;

import java.io.Serializable;

public class SearchResult{
    /** 分数 */
    /** score of result */
    public float mScore;
    /** 图片路径 */
    /** the path of image */
    public String mImagePath;
    /** 人脸框字符串，用来抠脸 */
    /** the face rectangle, use to crop face */
    public String mFaceRect;

    public SearchResult(String imagePath, String faceRect, float score) {
        mImagePath = imagePath;
        mFaceRect = faceRect;
        mScore = score;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "mScore=" + mScore +
                ", mImagePath='" + mImagePath + '\'' +
                ", mFaceRect='" + mFaceRect + '\'' +
                '}';
    }
}
