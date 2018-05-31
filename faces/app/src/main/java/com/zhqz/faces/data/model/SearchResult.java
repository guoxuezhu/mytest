package com.zhqz.faces.data.model;


public class SearchResult {
    /** 相识度分数 */
    /**
     * score of result
     */
    public float mScore;
    /** 图片路径 */
    /**
     * the path of image
     */
    public String mImagePath;
    public String name;
    public String sex;

    public SearchResult(float mScore, String mImagePath, String name, String sex) {
        this.mScore = mScore;
        this.mImagePath = mImagePath;
        this.name = name;
        this.sex = sex;
    }


    @Override
    public String toString() {
        return "SearchResult{" +
                "mScore=" + mScore +
                ", mImagePath='" + mImagePath + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
