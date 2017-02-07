package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Vinh on 10/18/2016.
 */

@Parcel
public class Article {

    @SerializedName("web_url")
    String webUrl;


    @SerializedName("headline")
    Headline headline;

    @SerializedName("multimedia")
    List<Multimedia> multimedia;

    @Parcel
    public static class Multimedia {

        @SerializedName("url")
        String url;

        public String getUrl() {
            return url;
        }
    }

    @Parcel
    public static class Headline {

        @SerializedName("main")
        String main;

        public String getHeadline() {
            return main;
        }
    }

    public String getHeadline() {
        return headline.getHeadline();
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    String thumbNail;

    @SerializedName("snippet")
    String snippet;

    public Article() {
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getSnippet() {
        return snippet;
    }
}