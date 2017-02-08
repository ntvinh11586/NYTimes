package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Headline {
    @SerializedName("main")
    String main;

    public String getHeadline() {
            return main;
        }
}