package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("docs")
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
}
