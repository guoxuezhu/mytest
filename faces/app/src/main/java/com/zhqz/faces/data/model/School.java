package com.zhqz.faces.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guoxuezhu on 18-3-27.
 */

public class School {

    @SerializedName("id")
    public int id;
    @SerializedName("value")
    public String value;


    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
