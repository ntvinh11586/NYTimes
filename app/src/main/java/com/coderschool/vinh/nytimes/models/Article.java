package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Article {
    @SerializedName("web_url")
    String webUrl;

    @SerializedName("headline")
    Headline headline;

    @SerializedName("multimedia")
    List<Multimedia> multimedia;

    @SerializedName("snippet")
    String snippet;

    public Article() {
    }

    public String getHeadline() {
        return headline.getHeadline();
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getSnippet() {
        return snippet;
    }
}