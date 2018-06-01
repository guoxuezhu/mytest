package com.zhqz.faces.data.model;


public class SearchResult {

    public long id;
    /**
     * 相识度分数
     */
    public float mScore;
    /**
     * 图片路径
     */
    public String mImagePath;
    public String name;
    public String sex;

    public SearchResult(long id, float mScore, String mImagePath, String name, String sex) {
        this.id = id;
        this.mScore = mScore;
        this.mImagePath = mImagePath;
        this.name = name;
        this.sex = sex;
    }

//    public SearchResult(float mScore, String mImagePath, String name, String sex) {
//        this.mScore = mScore;
//        this.mImagePath = mImagePath;
//        this.name = name;
//        this.sex = sex;
//    }


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
