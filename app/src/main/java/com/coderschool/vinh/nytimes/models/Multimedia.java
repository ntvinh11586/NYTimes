package com.coderschool.vinh.nytimes.models;

import com.coderschool.vinh.nytimes.utils.Constant;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Multimedia {
    @SerializedName("url")
    String url;

    public String getUrl() {
        return url;
    }

    public String getFullUrl() {
        return Constant.BASE_URL + url;
    }
}