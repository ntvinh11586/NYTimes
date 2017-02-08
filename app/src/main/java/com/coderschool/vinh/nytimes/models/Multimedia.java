package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Multimedia {
    @SerializedName("url")
    String url;

    public String getUrl() {
            return url;
        }
}